/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.robot;

/**
 * The RobotHealthData class encapsulates various health parameters of a robot
 * in the colony environment. It provides a structured way to monitor and update
 * the health status of robots, including their energy level, overall health,
 * operational status, and a descriptive status message.
 * <p>
 * This information is critical for maintenance and repair decisions, ensuring
 * that robots operate efficiently and are recharged or repaired when necessary.
 * </p>
 */
public class RobotHealthData {
    /** The current energy level of the robot (0-100). */
    private double energyLevel;
    /** The current health level of the robot (0-100). */
    private double healthyLevel;
    /** Indicates whether the robot is operational. */
    private boolean isOperational;
    /** A descriptive message indicating the robot's current status. */
    private String statusMessage;

    /**
     * Constructs a new RobotHealthData object with the specified initial values.
     *
     * @param energyLevel   the initial energy level (0-100)
     * @param healthyLevel  the initial health level (0-100)
     * @param isOperational true if the robot is operational, false otherwise
     * @param statusMessage a descriptive status message
     */
    public RobotHealthData(double energyLevel, double healthyLevel, boolean isOperational, String statusMessage) {
        this.energyLevel = energyLevel;
        this.healthyLevel = healthyLevel;
        this.isOperational = isOperational;
        this.statusMessage = statusMessage;
    }

    /**
     * Returns the current energy level.
     *
     * @return the energy level as a percentage (0-100)
     */
    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Returns whether the robot is operational.
     *
     * @return true if the robot is operational, false otherwise
     */
    public boolean isOperational() {
        return isOperational;
    }

    /**
     * Returns the current health level.
     *
     * @return the health level as a percentage (0-100)
     */
    public double getHealthyLevel() {
        return healthyLevel;
    }

    /**
     * Returns the status message.
     *
     * @return the status message string
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the energy level.
     *
     * @param energyLevel the new energy level (0-100)
     */
    public void setEnergyLevel(double energyLevel) {
        this.energyLevel = energyLevel;
    }

    /**
     * Sets the operational status.
     *
     * @param isOperational true if the robot is operational, false otherwise
     */
    public void setOperational(boolean isOperational) {
        this.isOperational = isOperational;
    }

    /**
     * Sets the health level.
     *
     * @param healthyLevel the new health level (0-100)
     */
    public void setHealthyLevel(double healthyLevel) {
        this.healthyLevel = healthyLevel;
    }

    /**
     * Sets the status message.
     *
     * @param statusMessage the new status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Returns a string representation of this RobotHealthData.
     *
     * @return a string summarizing the health data.
     */
    @Override
    public String toString() {
        return "RobotHealthData{" +
                "energyLevel=" + energyLevel +
                ", healthyLevel=" + healthyLevel +
                ", isOperational=" + isOperational +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
