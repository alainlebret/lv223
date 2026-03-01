/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.robot;

/**
 * Represents the damage status of various subsystems of a robot.
 * <p>
 * Each damage level is represented by an integer value ranging from 0 (no damage)
 * to 100 (completely non-functional, i.e. "kaputt" :) ). The class currently tracks
 * sensor damage and mobility damage, which can be used to adjust the robot's behavior
 * (e.g., additional movement cost or degraded sensor performance).
 * </p>
 */
public class InjuryStatus {

    /**
     * Sensor damage level (0 = no damage, 100 = completely non-functional).
     */
    private int sensorDamage;

    /**
     * Mobility damage level (0 = no damage, 100 = completely non-functional).
     * This value can be used to compute extra movement costs.
     */
    private int mobilityDamage;

    // Additional damage fields (e.g., communicationDamage) can be added here if needed.

    /**
     * Constructs a new InjuryStatus with no damage.
     */
    public InjuryStatus() {
        this.sensorDamage = 0;
        this.mobilityDamage = 0;
    }

    /**
     * Returns the current sensor damage level.
     *
     * @return the sensor damage level (0 to 100)
     */
    public int getSensorDamage() {
        return sensorDamage;
    }

    /**
     * Sets the sensor damage level.
     *
     * @param sensorDamage the sensor damage level to set (0 to 100)
     */
    public void setSensorDamage(int sensorDamage) {
        this.sensorDamage = sensorDamage;
    }

    /**
     * Returns the current mobility damage level.
     *
     * @return the mobility damage level (0 to 100)
     */
    public int getMobilityDamage() {
        return mobilityDamage;
    }

    /**
     * Sets the mobility damage level.
     *
     * @param mobilityDamage the mobility damage level to set (0 to 100)
     */
    public void setMobilityDamage(int mobilityDamage) {
        this.mobilityDamage = mobilityDamage;
    }
    
    /**
     * Determines if the robot's sensors are functional.
     * <p>
     * Sensors are considered functional if the sensor damage level is less than 50.
     * </p>
     *
     * @return {@code true} if sensors are functional, {@code false} otherwise.
     */
    public boolean sensorsFunctional() {
        return sensorDamage < 50;
    }

    /**
     * Calculates the additional movement cost based on mobility damage.
     * <p>
     * If the mobility damage is 50 or higher, an extra movement cost of 1 turn is applied.
     * Otherwise, no extra cost is incurred.
     * </p>
     *
     * @return the additional movement cost (e.g., extra turn count)
     */
    public int additionalMovementCost() {
        return mobilityDamage >= 50 ? 1 : 0;
    }
}
