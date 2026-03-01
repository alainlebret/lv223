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
 * Represents the melancholy health state of the planet.
 * <p>
 * This state is characterized by reduced vitality and resource regeneration,
 * reflecting a downcast or depressed environmental condition.
 * The implementation of {@code HealthState} in this class provides tailored logic
 * for when the planet is in a suboptimal, melancholy condition.
 * </p>
 *
 * @version 1.3 Revised
 * @since 1.0
 */
public class MelancholyHealthState implements HealthState {

    /** Random number generator for generating numerical health values. */
    private final Random random = new Random();

    /**
     * The last generated numerical value for the melancholy health state (range: 40-79).
     * <p>
     * Note: This value is recalculated on each call to {@code getNumericalValue()}.
     * For a consistent value within a simulation turn, consider caching this value.
     * </p>
     */
    private int lastGeneratedValue;

    /**
     * Updates the planet's health state based on seasonal conditions.
     * <p>
     * If the current season is SPRING or SUMMER, the health state transitions to a
     * {@link GoodHealthState}. Otherwise, melancholy effects are applied to the planet.
     * </p>
     *
     * @param planet the planet whose health is being updated; must not be {@code null}
     * @throws IllegalArgumentException if {@code planet} is {@code null}
     */
    @Override
    public void updateHealth(Planet planet) {
        if (planet == null) {
            throw new IllegalArgumentException("Planet cannot be null");
        }
        SeasonHandler seasonHandler = planet.getSeasonHandler();
        Season currentSeason = seasonHandler.getCurrentSeason();

        // Transition to GoodHealthState if the season is SPRING or SUMMER.
        if (currentSeason == Season.SPRING || currentSeason == Season.SUMMER) {
            planet.setHealthState(new GoodHealthState());
        } else {
            // Otherwise, apply melancholy effects.
            applyMelancholyEffects(planet);
        }
    }

    /**
     * Adjusts the base metamorphosis probability for the melancholy state.
     * <p>
     * In the melancholy state, the metamorphosis probability is increased by 20%.
     * </p>
     *
     * @param baseProbability the base probability of metamorphosis
     * @return the adjusted probability (base probability multiplied by 1.2)
     */
    @Override
    public double adjustMetamorphosisProbability(double baseProbability) {
        return baseProbability * 1.2;
    }

    /**
     * Applies melancholy-specific effects to the planet.
     * <p>
     * This method iterates over the planet's grid and decreases resource regeneration for certain cell types.
     * Specifically, for cells of type {@code FRUITS_AND_VEGETABLES} with positive resource units, it slightly reduces the
     * resource count; for {@code WATER} cells, it boosts regeneration slightly (capped by the cell's maximum).
     * </p>
     *
     * @param planet the planet on which to apply melancholy effects; must not be {@code null}
     */
    private void applyMelancholyEffects(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Cell cell = grid[y][x];
                double r = random.nextDouble();
                
                // For FRUITS_AND_VEGETABLES cells, slightly reduce resource units if available.
                if (cell.getType() == CellType.FRUITS_AND_VEGETABLES && cell.getUnits() > 0 && r < 0.5) {
                    int newUnits = (int) Math.round(cell.getUnits() * 0.999);
                    cell.setUnits(newUnits);
                }
                // For WATER cells, if there is any resource and with 50% chance, increase resource slightly.
                if (cell.getType() == CellType.WATER && cell.getUnits() > 0 && r < 0.5) {
                    int maxUnits = Cell.MAX_RESOURCE_UNITS.get(cell.getType());
                    int increasedUnits = (int) (cell.getUnits() * 1.001);
                    cell.setUnits(Math.min(increasedUnits, maxUnits));
                }
            }
        }
    }

    /**
     * Generates and returns a random numerical value representing the melancholy health state.
     * <p>
     * The value is randomly generated in the range [40, 79]. For consistency within a simulation turn,
     * consider caching the generated value.
     * </p>
     *
     * @return a random integer between 40 and 79 (inclusive)
     */
    @Override
    public int getNumericalValue() {
        lastGeneratedValue = 40 + random.nextInt(40); // Generates a value between 40 and 79.
        return lastGeneratedValue;
    }

    /**
     * Returns a textual representation of the melancholy health state.
     *
     * @return the string "Melancholy"
     */
    @Override
    public String toString() {
        return "Melancholy";
    }
}
