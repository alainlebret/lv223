/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.healthstate;

import fr.ensicaen.lv223.planet.Planet;

/**
 * Defines the behavior for various health states of a planet.
 * <p>
 * Implementations of this interface represent distinct conditions of planetary health,
 * which influence factors such as resource regeneration, event frequency, and overall
 * environmental stability. Implementing classes are responsible for updating the planet's
 * health state based on dynamic factors (e.g., extraction events, seasonal changes) and
 * for providing a numerical representation of the current health condition.
 * </p>
 *
 * @version 1.1 Revised
 * @since 1.0
 */
public interface HealthState {

    /**
     * Updates the planet's health state based on current conditions.
     * <p>
     * Implementations should adjust the planet's state in response to dynamic factors
     * such as extraction events or seasonal variations.
     * </p>
     *
     * @param planet the planet to update; must not be {@code null}
     * @throws IllegalArgumentException if {@code planet} is {@code null}
     */
    void updateHealth(Planet planet);

    /**
     * Adjusts the base metamorphosis probability to reflect the current health state.
     * <p>
     * This method applies an adjustment factor to the given base probability to account
     * for the current health condition of the planet.
     * </p>
     *
     * @param baseProbability the base probability of metamorphosis
     * @return the adjusted probability reflecting the health state
     */
    double adjustMetamorphosisProbability(double baseProbability);

    /**
     * Returns a numerical value representing the current health condition of the planet.
     * <p>
     * This value can be used to quantify the planet's health (e.g., for scaling metamorphosis
     * probabilities or comparing different health states). Note that the value may be
     * computed dynamically on each call.
     * </p>
     *
     * @return a numerical value representing the current health state
     */
    int getNumericalValue();
}
