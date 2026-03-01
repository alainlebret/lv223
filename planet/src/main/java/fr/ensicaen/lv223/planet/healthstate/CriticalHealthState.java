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
 * Represents the critical health state of the planet.
 * <p>
 * In this state, the planet faces severe challenges such as resource depletion
 * and adverse events. This class defines behaviors for the critical condition,
 * including state transitions, application of negative effects, and criteria for
 * declaring the planet "dead."
 * </p>
 *
 * @version 1.2 Revised
 * @since 1.0
 * @see HealthState
 */
public class CriticalHealthState implements HealthState {

    /** Random number generator for generating health values. */
    private final Random random = new Random();

    /**
     * The last generated numerical health value.
     * <p>
     * Note: A new value is generated on each call to {@link #getNumericalValue()}.
     * </p>
     */
    private int lastGeneratedValue;

    /** Flag indicating whether the planet has been declared dead. */
    private boolean isDead = false;

    /**
     * Updates the planet's health state based on the current conditions.
     * <p>
     * If the extraction event count is within the medium range, the state may transition
     * to an unstable state. If conditions indicate the planet is dead, it handles the death process.
     * Otherwise, the critical state effects are applied.
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
        
        if (shouldTransitionToUnstable(planet)) {
            planet.setHealthState(new UnstableHealthState());
        } else if (isPlanetDead(planet)) {
            handlePlanetDeath(planet);
        }
        
        applyCriticalEffects(planet);
    }

    /**
     * Adjusts the metamorphosis probability by multiplying the base probability by 10,
     * ensuring that it does not exceed 100%.
     *
     * @param baseProbability the base probability of metamorphosis
     * @return the adjusted probability, capped at 100.0
     */
    @Override
    public double adjustMetamorphosisProbability(double baseProbability) {
        return Math.min(baseProbability * 10, 100.0);
    }

    /**
     * Determines if the planet should transition to an unstable health state.
     *
     * @param planet the planet to evaluate
     * @return {@code true} if the extraction event count is within the medium range, {@code false} otherwise
     */
    private boolean shouldTransitionToUnstable(Planet planet) {
        int extractionCount = planet.getExtractionEventCount();
        return extractionCount >= Planet.MEDIUM_EVENT_THRESHOLD && extractionCount < Planet.HIGH_EVENT_THRESHOLD;
    }

    /**
     * Determines if the planet is declared dead.
     *
     * @param planet the planet to evaluate
     * @return {@code true} if the planet is dead, {@code false} otherwise
     */
    private boolean isPlanetDead(Planet planet) {
        if (isDead) {
            return true;
        }
        return planet.getExtractionEventCount() >= Planet.HIGH_EVENT_THRESHOLD * 2;
    }

    /**
     * Handles the death of the planet by converting all cells (except the base)
     * to stone and setting their resource units to 0.
     *
     * @param planet the planet to process
     */
    private void handlePlanetDeath(Planet planet) {
        for (Cell[] row : planet.getGrid()) {
            for (Cell cell : row) {
                // Preserve the base cell
                if (cell.getType() != CellType.BASE) {
                    cell.setType(CellType.STONE);
                    cell.setUnits(0);
                }
            }
        }
        isDead = true;
    }

    /**
     * Applies the negative effects of the critical state to the planet.
     * <p>
     * For every cell that is neither stone nor base, the resource units are reduced by 10%.
     * </p>
     *
     * @param planet the planet to process
     */
    private void applyCriticalEffects(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Cell cell = grid[y][x];
                if (cell.getType() != CellType.STONE && cell.getType() != CellType.BASE) {
                    int newUnits = (int) (cell.getUnits() * 0.97); // Reduce by 3%
                    cell.setUnits(newUnits);
                }
            }
        }
    }

    /**
     * Returns a numerical value representing the planet's current critical health level.
     * <p>
     * A new random value between 1 and 20 is generated on each call.
     * </p>
     *
     * @return a value between 1 and 20 representing the health level in the critical state
     */
    @Override
    public int getNumericalValue() {
        lastGeneratedValue = 1 + random.nextInt(20);
        return lastGeneratedValue;
    }

    /**
     * Returns a textual representation of the critical health state.
     *
     * @return the string "Critical"
     */
    @Override
    public String toString() {
        return "Critical";
    }
}
