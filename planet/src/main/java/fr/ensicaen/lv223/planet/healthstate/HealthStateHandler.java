/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.healthstate;

import fr.ensicaen.lv223.planet.Planet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the health state of a planet.
 * <p>
 * This class serves as the central context for planetary health state strategies,
 * facilitating transitions between different health states based on dynamic conditions.
 * </p>
 *
 * @version 1.3 Revised
 * @since 1.0
 */
public class HealthStateHandler {
    private static final Logger logger = LogManager.getLogger(HealthStateHandler.class);

    /** The current health state of the planet. */
    private HealthState currentHealthState;

    /**
     * Constructs a new HealthStateHandler with an initial state of good health.
     * The initial state is set to {@link GoodHealthState}.
     */
    public HealthStateHandler() {
        this.currentHealthState = new GoodHealthState();
        logger.info("Initialized HealthStateHandler with initial state: {}", currentHealthState);
    }

    /**
     * Updates the planet's health using the current health state.
     * <p>
     * Delegates the health update to the current {@code HealthState} implementation.
     * </p>
     *
     * @param planet the planet to update; must not be {@code null}
     * @throws IllegalArgumentException if {@code planet} is {@code null}
     */
    public void updateHealth(Planet planet) {
        if (planet == null) {
            throw new IllegalArgumentException("Planet cannot be null");
        }
        currentHealthState.updateHealth(planet);
    }

    /**
     * Retrieves the current health state of the planet.
     *
     * @return the current {@code HealthState} instance
     */
    public HealthState getHealthState() {
        return currentHealthState;
    }

    /**
     * Sets a new health state for the planet.
     * <p>
     * Logs the transition from the old health state to the new one.
     * </p>
     *
     * @param newHealthState the new {@code HealthState} to set; must not be {@code null}
     * @throws IllegalArgumentException if {@code newHealthState} is {@code null}
     */
    public void setHealthState(HealthState newHealthState) {
        if (newHealthState == null) {
            throw new IllegalArgumentException("newHealthState cannot be null");
        }
        logger.info("Transitioning health state from {} to {}", currentHealthState, newHealthState);
        this.currentHealthState = newHealthState;
    }

    /**
     * Returns a numerical value representing the current health state.
     * <p>
     * This value, provided by the current {@code HealthState}, is typically used
     * to adjust parameters such as metamorphosis probability.
     * </p>
     *
     * @return an integer representing the current health state (typically in the range 1 to 100)
     */
    public int getCurrentHealthNumericalValue() {
        return currentHealthState.getNumericalValue();
    }

    /**
     * Provides a textual description of the current health state.
     *
     * @return a {@code String} describing the current health state
     */
    public String getCurrentHealthDescription() {
        return currentHealthState.toString();
    }
}
