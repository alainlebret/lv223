/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.ensicaen.lv223.planet.exception.InvalidQuantityException;
import fr.ensicaen.lv223.planet.healthstate.CriticalHealthState;
import fr.ensicaen.lv223.planet.healthstate.HealthState;
import fr.ensicaen.lv223.planet.healthstate.HealthStateHandler;
import fr.ensicaen.lv223.planet.healthstate.MelancholyHealthState;
import fr.ensicaen.lv223.planet.healthstate.UnstableHealthState;
import fr.ensicaen.lv223.planet.metamorphosis.MetamorphosisEvent;
import fr.ensicaen.lv223.planet.metamorphosis.MetamorphosisHandler;
import fr.ensicaen.lv223.planet.metamorphosis.MetamorphosisStrategy;
import fr.ensicaen.lv223.planet.metamorphosis.StandardMetamorphosisStrategy;
import fr.ensicaen.lv223.planet.season.Season;
import fr.ensicaen.lv223.planet.season.SeasonHandler;
import fr.ensicaen.lv223.planet.utils.PlanetConfigurationLoader;
import fr.ensicaen.lv223.planet.utils.Config;

/**
 * Represents the central agent of the planet in the lv223 simulation project.
 * <p>
 * This class models the planet's ecosystem including its terrain grid, water resources,
 * seasons, health states, and metamorphosis events. It manages state transitions based on
 * extraction events, seasonal changes, and metamorphosis effects. Simulation progression is
 * tracked via discrete turns.
 * </p>
 * 
 * @since 1.0
 */
public class Planet {
    
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(Planet.class);

    public static final int DAYS_PER_YEAR = 364;
    public static final int SEASONS_PER_YEAR = 4;
    
    // Thresholds for cumulative mining events triggering health state changes.
    // LOW  : extractionEventCount below which Unstable reverts to Melancholy.
    // MEDIUM: extractionEventCount at which the planet enters Unstable.
    // HIGH  : extractionEventCount at which the planet enters Critical (death at HIGH×2).
    public static final int LOW_EVENT_THRESHOLD    = 30;
    public static final int MEDIUM_EVENT_THRESHOLD = 60;
    public static final int HIGH_EVENT_THRESHOLD   = 100;
    
    // Limits for metamorphosis events (number of cells changed)
    public static final int MAX_CELLS_TO_CHANGE_PER_METAMORPHOSIS = 1;
    public static final int MAX_CELLS_TO_CHANGE_PER_NEGATIVE_METAMORPHOSIS = 2;

    // Per-cell water recharge rates (units/turn) by season type
    private static final int WATER_CELL_RECHARGE_RATE_COLD_SEASON = 50; // AUTUMN / WINTER
    private static final int WATER_CELL_RECHARGE_RATE_WARM_SEASON  = 10; // SPRING / SUMMER

    /**
     * Scale factor between a water cell's resource units and the global capacity
     * accounting unit: one capacity unit represents this many cell resource units.
     * (MAX_RESOURCE_UNITS[WATER] = 10 000; maxWaterCapacity counts cells × 100.)
     */
    private static final double WATER_CAPACITY_SCALE = 100.0;

    /**
     * Probability per turn that a full WATER cell converts an adjacent STONE cell
     * into a new WATER cell (spring emergence). Only applies in AUTUMN / WINTER.
     * At 0.1 % per candidate pair, a new spring emerges roughly once per season
     * for each full water cell that borders stone terrain.
     */
    private static final double WATER_SPREAD_PROBABILITY = 0.001;
    
    // Duration constants
    public static final int SEASON_DURATION = DAYS_PER_YEAR / SEASONS_PER_YEAR;
    public static final long SEASON_DURATION_THRESHOLD_IN_MS = (long) SEASON_DURATION * 1000;
    
    /** 2D grid representing the planet’s cells. */
    private Cell[][] grid;
    
    /** Handles seasonal transitions. */
    private final SeasonHandler seasonHandler;
    
    /** Handles the planet's health state transitions. */
    private final HealthStateHandler healthStateHandler;
    
    /** Handles metamorphosis events on the planet. */
    private final MetamorphosisHandler metamorphosisHandler;
    
    /** Current simulation turn (each turn represents one day). */
    private int currentTurn;
    
    /** Days elapsed since the last metamorphosis event. */
    private int daysSinceLastMetamorphosis = 0;
    
    /** Global probability (0 to 1) of cell metamorphosis events. */
    private double metamorphosisProbability;
    
    /** Memory of recent metamorphosis events. */
    private final Queue<MetamorphosisEvent> metamorphosisMemory = new LinkedList<>();
    
    /** Total number of extraction events recorded on the planet. */
    private int extractionEventCount = 0;
    
    /** Maximum water capacity based on the number of water cells. */
    private double maxWaterCapacity;
    
    /** Current total water quantity on the planet. */
    private double totalWaterQuantity;
    
    /** Flag indicating whether there are significant changes in the grid. */
    private boolean hasSignificantChanges;

    /** Random number generator used for stochastic planet events. */
    private final Random random = new Random();

    /**
     * Constructs a new Planet using the provided configuration.
     * <p>
     * This constructor loads the grid configuration from a JSON file, initializes the
     * water capacity, season and health state handlers, and sets the initial metamorphosis
     * probability.
     * </p>
     *
     * @param config the simulation configuration parameters
     * @throws IllegalStateException if the configuration cannot be loaded
     */
    public Planet(Config config) {
        String configFilePath = config.getConfigPlanetFilePath().toString();
        logger.debug("Creating new planet with config file: {}", configFilePath);
        
        try {
            grid = PlanetConfigurationLoader.loadConfiguration(configFilePath);
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", configFilePath, e);
            throw new IllegalStateException("Failed to load planet configuration", e);
        }
        if (grid == null) {
            throw new IllegalStateException("Planet grid is null. Check configuration file.");
        }
        
        maxWaterCapacity = calculateMaxTotalWaterQuantity();
        totalWaterQuantity = maxWaterCapacity;
        
        seasonHandler = new SeasonHandler(SEASON_DURATION);
        healthStateHandler = new HealthStateHandler();
        try {
            metamorphosisHandler = new MetamorphosisHandler(
                    new StandardMetamorphosisStrategy(), 
                    calculateInitialMetamorphosisProbability());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize metamorphosis handler", e);
        }
        
        currentTurn = 0;
        hasSignificantChanges = false;
        
        // Mark the central cell as visited.
        grid[getHeight() / 2][getWidth() / 2].setVisited(true);
    }
    
    /**
     * Calculates the maximum total water capacity based on water cells in the grid.
     *
     * @return the maximum water capacity
     */
    private double calculateMaxTotalWaterQuantity() {
        double capacity = 0;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.getType() == CellType.WATER) {
                    capacity += 100; // Assumed capacity per water cell.
                }
            }
        }
        return capacity;
    }
    
    /**
     * Returns the grid height.
     *
     * @return the number of rows in the grid
     */
    public int getHeight() {
        if (grid == null) {
            throw new IllegalStateException("Planet grid not initialized.");
        }
        return grid.length;
    }
    
    /**
     * Returns the grid width.
     *
     * @return the number of columns in the grid
     */
    public int getWidth() {
        return (grid != null && grid.length > 0) ? grid[0].length : 0;
    }
    
    /**
     * Returns the current simulation year.
     *
     * @return the current year, computed from the turn count
     */
    public int getCurrentYear() {
        return currentTurn / DAYS_PER_YEAR;
    }
    
    /**
     * Returns the current simulation turn.
     *
     * @return the current turn (day)
     */
    public int getCurrentTurn() {
        return currentTurn;
    }
    
    /**
     * Returns the number of metamorphosis events recorded in memory.
     *
     * @return the metamorphosis event count
     */
    public int getMetamorphosisCount() {
        return metamorphosisMemory.size();
    }
    
    /**
     * Returns the planet grid.
     *
     * @return the 2D array of cells
     */
    public Cell[][] getGrid() {
        return grid;
    }
    
    /**
     * Returns the current metamorphosis probability.
     *
     * @return the probability value (0 to 1)
     */
    public double getMetamorphosisProbability() {
        return metamorphosisProbability;
    }
    
    /**
     * Returns the total extraction event count.
     *
     * @return the extraction event count
     */
    public int getExtractionEventCount() {
        return extractionEventCount;
    }
    
    /**
     * Returns the season handler.
     *
     * @return the season handler instance
     */
    public SeasonHandler getSeasonHandler() {
        return seasonHandler;
    }
    
    /**
     * Returns the health state handler.
     *
     * @return the health state handler instance
     */
    public HealthStateHandler getHealthStateHandler() {
        return healthStateHandler;
    }
    
    /**
     * Indicates whether significant changes have occurred in the grid.
     *
     * @return true if significant changes are detected, false otherwise
     */
    public boolean hasSignificantChanges() {
        return hasSignificantChanges;
    }
    
    /**
     * Disables the flag indicating significant grid changes.
     */
    public void disableHasSignificantChanges() {
        hasSignificantChanges = false;
    }
    
    /**
     * Advances the simulation by one turn.
     * <p>
     * This method performs seasonal updates, water level adjustments, applies extraction-
     * induced metamorphosis, updates the planet's health state, and increments the turn counters.
     * </p>
     */
    public void nextTurn() {
        logger.debug("Starting nextTurn(): turn {}", currentTurn);
        
        // Handle seasonal changes and metamorphosis
        if (seasonHandler.shouldChangeSeason(currentTurn)) {
            logger.debug("Season change triggered at turn {}", currentTurn);
            seasonHandler.changeSeason();
            Season newSeason = seasonHandler.getCurrentSeason();
            metamorphosisHandler.applySeasonalChanges(this, newSeason);
            logger.debug("Season changed to {}", newSeason);
        }

        // Update water levels based on seasonal recharge rate
        Season currentSeason = seasonHandler.getCurrentSeason();
        updateWaterLevels(currentSeason);
        rechargeWaterCells(currentSeason);
        spreadWaterToStone(currentSeason);

        // Apply extraction-induced metamorphosis changes
        double mineralExtractionIntensity = calculateMineralExtractionIntensity();
        double waterPumpingIntensity = calculateWaterPumpingIntensity();
        if (mineralExtractionIntensity > 0 || waterPumpingIntensity > 0) {
            metamorphosisHandler.applyExtractionChanges(this, mineralExtractionIntensity, waterPumpingIntensity);
        }
        
        // Update health state
        healthStateHandler.updateHealth(this);
        
        // Slowly heal the planet: reduce extraction stress every 5 turns.
        decayExtractionCount();

        // Increment simulation counters
        incrementTurnCounters();
        logger.debug("Finished nextTurn(): turn {}", currentTurn);
    }
    
    /**
     * Increases water levels based on the current season's recharge rate.
     *
     * @param currentSeason the current season
     */
    private void updateWaterLevels(Season currentSeason) {
        double rechargeRate = calculateSeasonalRechargeRate(currentSeason);
        totalWaterQuantity += rechargeRate;
        totalWaterQuantity = Math.min(totalWaterQuantity, maxWaterCapacity);
    }
    
    /**
     * Calculates the seasonal water recharge rate.
     *
     * @param season the current season
     * @return the recharge rate per turn
     */
    private double calculateSeasonalRechargeRate(Season season) {
        switch (season) {
            case AUTUMN:
            case WINTER:
                return 4.0 / DAYS_PER_YEAR;
            default:
                return 1.0 / DAYS_PER_YEAR;
        }
    }
    
    /**
     * Refills individual WATER cells each turn according to the seasonal recharge rate.
     * <p>
     * AUTUMN and WINTER bring rainfall and aquifer recharge; SPRING and SUMMER provide
     * a slower natural seepage. Each partially depleted WATER cell gains resource units
     * up to its maximum capacity. The global water budget ({@code totalWaterQuantity})
     * is updated proportionally so that {@link #calculateWaterPumpingIntensity()} keeps
     * reflecting the true hydration level of the planet.
     * </p>
     *
     * @param season the current season
     */
    private void rechargeWaterCells(Season season) {
        int rechargePerCell = (season == Season.AUTUMN || season == Season.WINTER)
                ? WATER_CELL_RECHARGE_RATE_COLD_SEASON
                : WATER_CELL_RECHARGE_RATE_WARM_SEASON;
        int maxUnits = Cell.MAX_RESOURCE_UNITS.get(CellType.WATER);

        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.getType() == CellType.WATER && cell.getUnits() < maxUnits) {
                    int added = Math.min(rechargePerCell, maxUnits - cell.getUnits());
                    cell.setUnits(cell.getUnits() + added);
                    totalWaterQuantity += added / WATER_CAPACITY_SCALE;
                }
            }
        }
        totalWaterQuantity = Math.min(totalWaterQuantity, maxWaterCapacity);
        logger.debug("Water cells recharged ({} units/cell, season {}).", rechargePerCell, season);
    }

    /**
     * Spreads water from full WATER cells into adjacent STONE cells, simulating
     * the emergence of new springs when aquifers overflow.
     * <p>
     * This effect is only active during AUTUMN and WINTER, when precipitation and
     * groundwater pressure are at their peak. Each full WATER cell ({@code units ==
     * MAX_RESOURCE_UNITS[WATER]}) has a {@value #WATER_SPREAD_PROBABILITY} probability
     * per turn of converting each neighbouring STONE cell into a nascent WATER cell.
     * </p>
     * <p>
     * Candidates are collected in a read-only first pass, then converted in a second
     * pass to avoid visiting newly created WATER cells during the same turn. A
     * {@link HashSet} keyed by grid position ensures that a STONE cell bordered by
     * several full WATER cells is converted at most once per turn.
     * </p>
     *
     * @param season the current season
     */
    private void spreadWaterToStone(Season season) {
        if (season != Season.AUTUMN && season != Season.WINTER) {
            return;
        }

        int maxUnits = Cell.MAX_RESOURCE_UNITS.get(CellType.WATER);
        Set<String> scheduled = new HashSet<>();
        List<int[]> toConvert = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x].getType() != CellType.WATER || grid[y][x].getUnits() < maxUnits) {
                    continue;
                }
                // Full WATER cell: try each of the 8 neighbours
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        int nx = x + dx;
                        int ny = y + dy;
                        if (ny < 0 || ny >= grid.length || nx < 0 || nx >= grid[ny].length) continue;
                        String key = ny + "," + nx;
                        if (grid[ny][nx].getType() == CellType.STONE
                                && !scheduled.contains(key)
                                && random.nextDouble() < WATER_SPREAD_PROBABILITY) {
                            scheduled.add(key);
                            toConvert.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }

        for (int[] coords : toConvert) {
            int cx = coords[0];
            int cy = coords[1];
            grid[cy][cx].setType(CellType.WATER);
            grid[cy][cx].setUnits(WATER_CELL_RECHARGE_RATE_COLD_SEASON);
            maxWaterCapacity += WATER_CAPACITY_SCALE;
            totalWaterQuantity += WATER_CELL_RECHARGE_RATE_COLD_SEASON / WATER_CAPACITY_SCALE;
            hasSignificantChanges = true;
            logger.info("New spring emerged at ({}, {}): STONE converted to WATER.", cx, cy);
        }
    }

    /**
     * Calculates the water pumping intensity based on current water levels.
     *
     * @return a value between 0 and 100 representing the intensity
     */
    private double calculateWaterPumpingIntensity() {
        if (maxWaterCapacity <= 0) return 0;
        double intensity = ((maxWaterCapacity - totalWaterQuantity) / maxWaterCapacity) * 100;
        return Math.max(0, Math.min(intensity, 100));
    }
    
    /**
     * Calculates the mineral extraction intensity as a percentage.
     *
     * @return a value between 0 and 100 representing the extraction intensity
     */
    private double calculateMineralExtractionIntensity() {
        double intensity = ((double) extractionEventCount / HIGH_EVENT_THRESHOLD) * 100;
        return Math.max(0, Math.min(intensity, 100));
    }
    
    /**
     * Increments the simulation turn and days since last metamorphosis.
     */
    private void incrementTurnCounters() {
        currentTurn++;
        daysSinceLastMetamorphosis++;
    }

    /**
     * Gradually reduces the planet's accumulated extraction stress.
     * <p>
     * Every 5 turns the extraction event count is decremented by one (minimum 0),
     * simulating natural geological recovery. A colony that pauses or slows mining
     * will therefore see the planet's health improve over time, giving students an
     * incentive to manage resources sustainably rather than exhausting them outright.
     * </p>
     */
    private void decayExtractionCount() {
        if (currentTurn % 5 == 0 && extractionEventCount > 0) {
            extractionEventCount--;
            logger.debug("Extraction stress decayed to {}.", extractionEventCount);
        }
    }
    
    /**
     * Dynamically changes the metamorphosis strategy.
     *
     * @param newStrategy the new strategy to set
     */
    public void setChangeStrategy(MetamorphosisStrategy newStrategy) {
        metamorphosisHandler.setStrategy(newStrategy);
    }
    
    /**
     * Clears the metamorphosis events memory.
     */
    public void clearMetamorphosisMemory() {
        metamorphosisMemory.clear();
    }
    
    /**
     * Sets the metamorphosis probability (clamped between 0 and 1).
     *
     * @param probability the new probability value
     */
    public void setMetamorphosisProbability(double probability) {
        this.metamorphosisProbability = Math.min(Math.max(probability, 0.0), 1.0);
    }
    
    /**
     * Calculates the initial metamorphosis probability.
     *
     * @return the initial probability value
     */
    private double calculateInitialMetamorphosisProbability() {
        logger.warn("calculateInitialMetamorphosisProbability() is simplified; returning default value 0");
        return 0;
    }
    
    /**
     * Records a metamorphosis event.
     *
     * @param description a description of the event
     */
    public void recordMetamorphosisEvent(String description) {
        MetamorphosisEvent event = new MetamorphosisEvent(description);
        event.setTimestamp(System.currentTimeMillis());
        metamorphosisMemory.add(event);
        cleanOldMetamorphosisEvents();
    }
    
    /**
     * Sets the planet's health state.
     *
     * @param state the new health state
     */
    public void setHealthState(HealthState state) {
        healthStateHandler.setHealthState(state);
    }
    
    /**
     * Updates the planet's health state.
     */
    public void updateHealth() {
        healthStateHandler.getHealthState().updateHealth(this);
    }
    
    /**
     * Removes metamorphosis events older than a specified threshold.
     */
    private void cleanOldMetamorphosisEvents() {
        long currentTime = System.currentTimeMillis();
        metamorphosisMemory.removeIf(event -> currentTime - event.getTimestamp() > 10000);
    }
    
    /**
     * Records a mineral extraction event and adjusts the planet's health state.
     * <p>
     * The health state transition is driven by the <em>cumulative</em> extraction
     * event count, not by the amount extracted in a single action, so that a large
     * one-off extraction does not immediately throw the planet into a critical state.
     * </p>
     *
     * @param extractionIntensity the number of mineral units extracted in this action
     *                            (retained for future use; not used for state transitions)
     */
    public void recordExtractionEvent(int extractionIntensity) {
        extractionEventCount++;
        adjustHealthStateBasedOnExtractionIntensity(extractionEventCount);
    }
    
    /**
     * Records a water-pumping event by reducing the global water budget.
     * <p>
     * This keeps {@link #calculateWaterPumpingIntensity()} accurate: the more water
     * robots pump, the higher the pumping intensity, which in turn raises the
     * metamorphosis probability on the planet.
     * </p>
     *
     * @param units the number of water resource units that were pumped
     */
    public void recordPumpEvent(int units) {
        totalWaterQuantity -= units / WATER_CAPACITY_SCALE;
        totalWaterQuantity = Math.max(0, totalWaterQuantity);
        logger.debug("Pump event recorded: {} units pumped; totalWaterQuantity now {}.",
                units, totalWaterQuantity);
    }

    /**
     * Adjusts the planet's health state based on the cumulative extraction event count.
     *
     * @param extractionCount the current cumulative number of extraction events
     */
    private void adjustHealthStateBasedOnExtractionIntensity(int extractionCount) {
        if (extractionCount > HIGH_EVENT_THRESHOLD) {
            if (!(healthStateHandler.getHealthState() instanceof CriticalHealthState)) {
                setHealthState(new CriticalHealthState());
            }
        } else if (extractionCount > MEDIUM_EVENT_THRESHOLD) {
            if (!(healthStateHandler.getHealthState() instanceof UnstableHealthState)) {
                setHealthState(new UnstableHealthState());
            }
        } else {
            if (!(healthStateHandler.getHealthState() instanceof MelancholyHealthState)) {
                setHealthState(new MelancholyHealthState());
            }
        }
    }
}
