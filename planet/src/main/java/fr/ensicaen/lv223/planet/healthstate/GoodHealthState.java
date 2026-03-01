/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.healthstate;

import java.util.Random;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.season.Season;
import fr.ensicaen.lv223.planet.season.SeasonHandler;

/**
 * Represents the "good" health state of the planet.
 * <p>
 * In this state, the planet exhibits balanced resource management and favorable
 * environmental conditions, indicating optimal functionality. This class implements
 * the {@code HealthState} interface and provides behaviors that help maintain the planet’s
 * well-being. The numerical value representing the good health state is generated randomly
 * between 80 and 100 on each call; if a consistent value per simulation turn is desired,
 * consider caching the result.
 * </p>
 *
 * @version 1.3 Revised
 * @since 1.0
 * @see HealthState
 */
public class GoodHealthState implements HealthState {

    /** Random number generator for generating health values. */
    private final Random random = new Random();

    /**
     * The last generated numerical value for the good health state (range: 80 to 100).
     * <p>
     * Note: This value is recalculated on each call to {@link #getNumericalValue()}.
     * For a constant value within a simulation turn, consider caching this value.
     * </p>
     */
    private int lastGeneratedValue;

    /**
     * Updates the planet's health state based on extraction events and seasonal conditions.
     * <p>
     * The logic is as follows:
     * <ul>
     *   <li>If the extraction event count is equal to or exceeds the HIGH threshold,
     *       the planet transitions to a {@link CriticalHealthState}.</li>
     *   <li>If the extraction event count exceeds the MEDIUM threshold,
     *       the planet transitions to an {@link UnstableHealthState}.</li>
     *   <li>If the current season is {@code AUTUMN} or {@code WINTER},
     *       the planet transitions to a {@link MelancholyHealthState}.</li>
     *   <li>If none of these conditions apply, positive effects are applied to maintain good health.</li>
     * </ul>
     * </p>
     *
     * @param planet the planet to update; must not be {@code null}
     * @throws IllegalArgumentException if {@code planet} is {@code null}
     */
    @Override
    public void updateHealth(Planet planet) {
        if (planet == null) {
            throw new IllegalArgumentException("Planet cannot be null");
        }

        int extractionEvents = planet.getExtractionEventCount();
        SeasonHandler seasonHandler = planet.getSeasonHandler();
        Season currentSeason = seasonHandler.getCurrentSeason();

        if (extractionEvents >= Planet.HIGH_EVENT_THRESHOLD) {
            planet.setHealthState(new CriticalHealthState());
        } else if (extractionEvents > Planet.MEDIUM_EVENT_THRESHOLD) {
            planet.setHealthState(new UnstableHealthState());
        } else if (currentSeason == Season.AUTUMN || currentSeason == Season.WINTER) {
            planet.setHealthState(new MelancholyHealthState());
        } else {
            applyPositiveEffects(planet);
        }
    }

    /**
     * In the good health state, no adjustment is applied to the metamorphosis probability.
     *
     * @param baseProbability the base probability of metamorphosis
     * @return the unmodified base probability
     */
    @Override
    public double adjustMetamorphosisProbability(double baseProbability) {
        return baseProbability;
    }

    /**
     * Applies positive effects to the planet under good health.
     * <p>
     * Currently, this method enhances resource regeneration by boosting the production
     * of key resource cells. Future enhancements may include additional positive effects.
     * </p>
     *
     * @param planet the planet to process; must not be {@code null}
     */
    private void applyPositiveEffects(Planet planet) {
        enhanceResourceCells(planet);
    }

    /**
     * Enhances the regeneration of resource cells.
     * <p>
     * For each cell of type {@code FRUITS_AND_VEGETABLES}, the resource units are increased
     * by 10% of the current amount, capped at the maximum allowed units. For cells of type
     * {@code WATER} with a positive quantity, a slight decrease is applied.
     * </p>
     *
     * @param planet the planet to process; must not be {@code null}
     */
    private void enhanceResourceCells(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Cell cell = grid[y][x];
                double randomValue = random.nextDouble();

                if (cell.getType() == CellType.FRUITS_AND_VEGETABLES && randomValue < 0.5) {
                    int currentUnits = cell.getUnits();
                    int maxUnits = Cell.MAX_RESOURCE_UNITS.get(cell.getType());
                    int increasedUnits = (int) (currentUnits * 1.1);
                    cell.setUnits(Math.min(increasedUnits, maxUnits));
                } else if (cell.getType() == CellType.WATER && cell.getUnits() > 0 && randomValue < 0.5) {
                    int newUnits = (int) Math.round(cell.getUnits() * 0.999);
                    cell.setUnits(newUnits);
                }
            }
        }
    }

    /**
     * Returns a numerical value representing the planet's health level in the good health state.
     * <p>
     * A new random integer between 80 and 100 (inclusive) is generated on each call.
     * For a constant value within a simulation turn, consider caching this result.
     * </p>
     *
     * @return a random integer between 80 and 100 representing the health level
     */
    @Override
    public int getNumericalValue() {
        lastGeneratedValue = 80 + random.nextInt(21);
        return lastGeneratedValue;
    }

    /**
     * Returns a textual representation of the good health state.
     *
     * @return the string "Good"
     */
    @Override
    public String toString() {
        return "Good";
    }
}
