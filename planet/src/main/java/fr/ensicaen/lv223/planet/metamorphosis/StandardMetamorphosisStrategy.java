/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.metamorphosis;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.season.Season;

/**
 * Standard implementation of {@code MetamorphosisStrategy} that applies metamorphosis
 * changes based on seasonal transitions and resource extraction intensity.
 * <p>
 * Seasonal changes are applied probabilistically to random cells according to predefined
 * transformation rules, while extraction intensity triggers severe, moderate, or minor metamorphosis.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class StandardMetamorphosisStrategy implements MetamorphosisStrategy {

    private static final Logger logger = LogManager.getLogger(StandardMetamorphosisStrategy.class);
    private final Random rand = new Random();

    @Override
    public void applySeasonalChanges(Planet planet, Season season) {
        double seasonalProbability = calculateSeasonalMetamorphosisProbability(season);
        applySeasonalChangesOnRandomCells(planet, seasonalProbability);
    }

    @Override
    public void applyExtractionChanges(Planet planet, double extractionIntensity) {
        if (extractionIntensity > Planet.HIGH_EVENT_THRESHOLD) {
            triggerSevereMetamorphosis(planet);
        } else if (extractionIntensity > Planet.MEDIUM_EVENT_THRESHOLD) {
            triggerModerateMetamorphosis(planet);
        } else {
            triggerMinorMetamorphosis(planet);
        }
    }

    /**
     * Calculates the metamorphosis probability based on the current season.
     *
     * @param season the current season
     * @return the seasonal metamorphosis probability
     */
    private double calculateSeasonalMetamorphosisProbability(Season season) {
        switch (season) {
            case SPRING:
            case SUMMER:
                return 4.0; // Higher probability in Spring and Summer
            case AUTUMN:
            case WINTER:
                return 1.0; // Lower probability in Autumn and Winter
            default:
                return 0.0;
        }
    }

    /**
     * Applies seasonal metamorphosis changes on random cells of the planet.
     *
     * @param planet      the planet to transform
     * @param probability the probability factor determining the maximum number of cell changes
     */
    private void applySeasonalChangesOnRandomCells(Planet planet, double probability) {
        int maxChanges = (int) Math.ceil(probability);
        int changes = 0;
        Cell[][] grid = planet.getGrid();
        int height = grid.length;
        int width = grid[0].length;
        int attempts = 0;
        int maxAttempts = height * width * 2;
        
        while (changes < maxChanges && attempts < maxAttempts) {
            int x = calculateRandomIndexBetween(0, width - 1);
            int y = calculateRandomIndexBetween(0, height - 1);
            Cell cell = grid[y][x];
            
            // Skip WATER and BASE cells.
            if (cell.getType() == CellType.WATER || cell.getType() == CellType.BASE) {
                attempts++;
                continue;
            }
            
            CellType currentType = cell.getType();
            CellType newType = getSeasonalChangeType(planet, currentType);
            if (newType != currentType && rand.nextDouble() < 0.05) {
                recordMetamorphosisEvent(planet, x, y, newType);
                cell.setType(newType);
                // Reset resource units based on new cell type.
                if (newType == CellType.FRUITS_AND_VEGETABLES ||
                    newType == CellType.MINERAL ||
                    newType == CellType.FOREST) {
                    int maxUnits = Cell.MAX_RESOURCE_UNITS.get(newType);
                    cell.setUnits((int) (maxUnits * 0.5));
                } else {
                    cell.setUnits(0);
                }
                // Reset cell state flags.
                cell.setVisited(false);
                cell.setHasAlienConstructionOnIt(false);
                cell.setModified(true);
                changes++;
            }
            attempts++;
        }
    }

    /**
     * Records a metamorphosis event on the planet.
     *
     * @param planet  the planet on which the event occurred
     * @param x       the x-coordinate of the affected cell
     * @param y       the y-coordinate of the affected cell
     * @param newType the new cell type after metamorphosis
     */
    private void recordMetamorphosisEvent(Planet planet, int x, int y, CellType newType) {
        String eventDescription = String.format("Seasonal change at (%d, %d) to %s", x, y, newType);
        planet.recordMetamorphosisEvent(eventDescription);
    }

    /**
     * Determines the new cell type for a given cell based on seasonal change rules.
     *
     * @param planet      the planet (provides current season information)
     * @param currentType the current cell type
     * @return the new cell type as defined by the seasonal rules, or the current type if no change applies
     */
    private CellType getSeasonalChangeType(Planet planet, CellType currentType) {
        Map<Season, Map<CellType, CellType>> seasonalRules = getSeasonalChangeRules();
        Map<CellType, CellType> rulesForSeason = seasonalRules.getOrDefault(
                planet.getSeasonHandler().getCurrentSeason(), Collections.emptyMap());
        return rulesForSeason.getOrDefault(currentType, currentType);
    }

    /**
     * Provides a mapping of seasonal change rules.
     *
     * @return a map where each season is associated with a mapping of cell types to their seasonal transformation
     */
    private Map<Season, Map<CellType, CellType>> getSeasonalChangeRules() {
        Map<Season, Map<CellType, CellType>> rules = new EnumMap<>(Season.class);

        // Summer to Autumn rules
        Map<CellType, CellType> summerToAutumn = new EnumMap<>(CellType.class);
        summerToAutumn.put(CellType.FRUITS_AND_VEGETABLES, CellType.PRAIRIE);
        summerToAutumn.put(CellType.PRAIRIE, CellType.DRY_PRAIRIE);
        summerToAutumn.put(CellType.WET_PRAIRIE, CellType.PRAIRIE);
        rules.put(Season.SUMMER, summerToAutumn);

        // Autumn to Winter rules
        Map<CellType, CellType> autumnToWinter = new EnumMap<>(CellType.class);
        autumnToWinter.put(CellType.PRAIRIE, CellType.DRY_PRAIRIE);
        autumnToWinter.put(CellType.FOREST, CellType.WET_PRAIRIE);
        autumnToWinter.put(CellType.DRY_PRAIRIE, CellType.DESERT);
        rules.put(Season.AUTUMN, autumnToWinter);

        // Winter to Spring rules
        Map<CellType, CellType> winterToSpring = new EnumMap<>(CellType.class);
        winterToSpring.put(CellType.DESERT, CellType.DRY_PRAIRIE);
        winterToSpring.put(CellType.DRY_PRAIRIE, CellType.PRAIRIE);
        winterToSpring.put(CellType.PRAIRIE, CellType.FOREST);
        winterToSpring.put(CellType.WET_PRAIRIE, CellType.FRUITS_AND_VEGETABLES);
        rules.put(Season.WINTER, winterToSpring);

        // Spring to Summer rules
        Map<CellType, CellType> springToSummer = new EnumMap<>(CellType.class);
        springToSummer.put(CellType.DRY_PRAIRIE, CellType.PRAIRIE);
        springToSummer.put(CellType.PRAIRIE, CellType.FRUITS_AND_VEGETABLES);
        springToSummer.put(CellType.WET_PRAIRIE, CellType.FRUITS_AND_VEGETABLES);
        springToSummer.put(CellType.DESERT, CellType.DRY_PRAIRIE);
        rules.put(Season.SPRING, springToSummer);

        return rules;
    }

    /**
     * Returns a pseudorandom integer between the specified min and max, inclusive.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a pseudorandom integer between min and max
     * @throws IllegalArgumentException if max is less than min
     */
    private int calculateRandomIndexBetween(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max must be greater than or equal to min");
        }
        return rand.nextInt(max - min + 1) + min;
    }

    // Metamorphosis triggered by extraction events

    /**
     * Triggers severe metamorphosis by converting a number of cells to STONE.
     *
     * @param planet the planet to transform
     */
    private void triggerSevereMetamorphosis(Planet planet) {
        Cell[][] grid = planet.getGrid();
        int maxChanges = Planet.MAX_CELLS_TO_CHANGE_PER_NEGATIVE_METAMORPHOSIS;
        int changes = 0;

        for (int y = 0; y < grid.length && changes < maxChanges; y++) {
            for (int x = 0; x < grid[y].length && changes < maxChanges; x++) {
                Cell cell = grid[y][x];
                if (cell.getType() != CellType.STONE &&
                    cell.getType() != CellType.BASE &&
                    cell.getType() != CellType.IMPENETRABLE) {
                    if (rand.nextDouble() < 0.01) {
                        cell.setType(CellType.STONE);
                        cell.setUnits(0);
                        changes++;
                    }
                }
            }
        }
        logger.debug("Severe metamorphosis applied to {} cells", changes);
    }

    /**
     * Triggers moderate metamorphosis based on medium extraction intensity.
     *
     * @param planet the planet to transform
     */
    private void triggerModerateMetamorphosis(Planet planet) {
        Cell[][] grid = planet.getGrid();
        int changes = 0;
        int maxChanges = Planet.MAX_CELLS_TO_CHANGE_PER_METAMORPHOSIS;
        for (int y = 0; y < grid.length && changes < maxChanges; y++) {
            for (int x = 0; x < grid[y].length && changes < maxChanges; x++) {
                Cell cell = grid[y][x];
                if (cell.getType() == CellType.FOREST) {
                    cell.setType(CellType.PRAIRIE);
                    cell.setUnits(0);
                    changes++;
                }
            }
        }
        logger.debug("Moderate metamorphosis applied to {} cells", changes);
    }

    /**
     * Triggers minor metamorphosis based on low extraction intensity.
     *
     * @param planet the planet to transform
     */
    private void triggerMinorMetamorphosis(Planet planet) {
        Cell[][] grid = planet.getGrid();
        int changes = 0;
        int maxChanges = 1;
        for (int y = 0; y < grid.length && changes < maxChanges; y++) {
            for (int x = 0; x < grid[y].length && changes < maxChanges; x++) {
                Cell cell = grid[y][x];
                if (cell.getType() == CellType.PRAIRIE) {
                    cell.setUnits((int) (cell.getUnits() * 0.95));
                    changes++;
                }
            }
        }
        logger.debug("Minor metamorphosis applied to {} cells", changes);
    }
}
