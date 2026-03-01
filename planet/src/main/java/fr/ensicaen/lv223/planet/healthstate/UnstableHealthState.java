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

/**
 * Represents the unstable health state of the planet.
 * <p>
 * In this state, rapid and unpredictable ecological changes occur. Resource depletion
 * accelerates, and the planet may transition to either a critical or a melancholy state,
 * depending on the extraction activity.
 * </p>
 *
 * @version 1.3
 * @since 1.0
 */
public class UnstableHealthState implements HealthState {

    /** Random number generator for generating numerical health values. */
    private final Random random = new Random();

    /** 
     * The last generated numerical value for the unstable health state (range: 20 to 39).
     * <p>
     * Note: This value is regenerated on each call to {@code getNumericalValue()}. For consistency
     * within a simulation turn, consider caching this value.
     * </p>
     */
    private int lastGeneratedValue;

    /**
     * Updates the planet's health state by applying unstable state effects.
     * <p>
     * If extraction events are high (>= 30), the planet transitions to a {@link CriticalHealthState}.
     * If extraction events are very low (< 15), the planet transitions to a {@link MelancholyHealthState}.
     * Otherwise, unstable effects are applied to accelerate resource depletion.
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

        // Determine if the planet should transition based on extraction events.
        if (shouldTransitionToCritical(planet)) {
            planet.setHealthState(new CriticalHealthState());
            return;
        } else if (shouldTransitionToMelancholy(planet)) {
            planet.setHealthState(new MelancholyHealthState());
            return;
        }

        // Apply unstable effects if no transition occurs.
        applyUnstableEffects(planet);
    }

    /**
     * Determines whether the planet should transition to a critical health state.
     *
     * @param planet the planet to evaluate
     * @return {@code true} if the extraction count has reached the high threshold
     */
    private boolean shouldTransitionToCritical(Planet planet) {
        return planet.getExtractionEventCount() >= Planet.HIGH_EVENT_THRESHOLD;
    }

    /**
     * Determines whether the planet should transition to a melancholy health state.
     * <p>
     * Triggered when sustained mining pauses long enough for the planet's stress
     * count to fall below the low-event threshold, indicating partial recovery.
     * </p>
     *
     * @param planet the planet to evaluate
     * @return {@code true} if the extraction count has dropped below the low threshold
     */
    private boolean shouldTransitionToMelancholy(Planet planet) {
        return planet.getExtractionEventCount() < Planet.LOW_EVENT_THRESHOLD;
    }

    /**
     * Applies unstable state effects to the planet.
     * <p>
     * For each cell of type {@code FRUITS_AND_VEGETABLES} or {@code MINERAL} that contains resources,
     * the resource quantity is reduced by 5% per turn.
     * </p>
     *
     * @param planet the planet on which to apply unstable effects; must not be {@code null}
     */
    private void applyUnstableEffects(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Cell cell = grid[y][x];
                if ((cell.getType() == CellType.FRUITS_AND_VEGETABLES || cell.getType() == CellType.MINERAL)
                        && cell.getUnits() > 0) {
                    int newUnits = (int) Math.round(cell.getUnits() * 0.98); // Reduce by 2%
                    cell.setUnits(newUnits);
                }
            }
        }
    }

    /**
     * Adjusts the base metamorphosis probability in the unstable health state.
     * <p>
     * This implementation doubles the base probability but caps it at 100%.
     * </p>
     *
     * @param baseProbability the base probability of metamorphosis
     * @return the adjusted probability
     */
    @Override
    public double adjustMetamorphosisProbability(double baseProbability) {
        return Math.min(baseProbability * 2, 100.0);
    }

    /**
     * Generates and returns a random numerical value representing the current unstable health state.
     * <p>
     * The value is randomly generated in the range [20, 39]. For consistency within a simulation turn,
     * consider caching this value.
     * </p>
     *
     * @return a random integer between 20 and 39 (inclusive)
     */
    @Override
    public int getNumericalValue() {
        lastGeneratedValue = 20 + random.nextInt(20); // random.nextInt(20) produces 0 to 19
        return lastGeneratedValue;
    }

    /**
     * Returns a textual representation of the unstable health state.
     *
     * @return the string "Unstable"
     */
    @Override
    public String toString() {
        return "Unstable";
    }
}
