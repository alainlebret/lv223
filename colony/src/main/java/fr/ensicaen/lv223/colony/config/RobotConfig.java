/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the configuration for robot strategies and behavior.
 * Parameters such as thresholds, default action amounts, and colony base
 * coordinates are loaded from a JSON file (e.g., {@code /json/robot_config.json}).
 */
public class RobotConfig {

    private int mobilityDamageThreshold;
    private int sensorFunctionalityThreshold;
    private int defaultHarvestAmount;
    private int defaultMineAmount;
    private int defaultPumpAmount;
    private int defaultMoveDelay;
    private int minEnergyThreshold;
    private String repairReturnDirection;

    /** Global X coordinate of the colony base on the planet grid. */
    private int baseX;

    /** Global Y coordinate of the colony base on the planet grid. */
    private int baseY;

    // Getters and setters

    public int getMobilityDamageThreshold() {
        return mobilityDamageThreshold;
    }

    public void setMobilityDamageThreshold(int mobilityDamageThreshold) {
        this.mobilityDamageThreshold = mobilityDamageThreshold;
    }

    public int getSensorFunctionalityThreshold() {
        return sensorFunctionalityThreshold;
    }

    public void setSensorFunctionalityThreshold(int sensorFunctionalityThreshold) {
        this.sensorFunctionalityThreshold = sensorFunctionalityThreshold;
    }

    public int getDefaultHarvestAmount() {
        return defaultHarvestAmount;
    }

    public void setDefaultHarvestAmount(int defaultHarvestAmount) {
        this.defaultHarvestAmount = defaultHarvestAmount;
    }

    public int getDefaultMineAmount() {
        return defaultMineAmount;
    }

    public void setDefaultMineAmount(int defaultMineAmount) {
        this.defaultMineAmount = defaultMineAmount;
    }

    public int getDefaultPumpAmount() {
        return defaultPumpAmount;
    }

    public void setDefaultPumpAmount(int defaultPumpAmount) {
        this.defaultPumpAmount = defaultPumpAmount;
    }

    public int getDefaultMoveDelay() {
        return defaultMoveDelay;
    }

    public void setDefaultMoveDelay(int defaultMoveDelay) {
        this.defaultMoveDelay = defaultMoveDelay;
    }

    public int getMinEnergyThreshold() {
        return minEnergyThreshold;
    }

    public void setMinEnergyThreshold(int minEnergyThreshold) {
        this.minEnergyThreshold = minEnergyThreshold;
    }

    public String getRepairReturnDirection() {
        return repairReturnDirection;
    }

    public void setRepairReturnDirection(String repairReturnDirection) {
        this.repairReturnDirection = repairReturnDirection;
    }

    public int getBaseX() {
        return baseX;
    }

    public void setBaseX(int baseX) {
        this.baseX = baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public void setBaseY(int baseY) {
        this.baseY = baseY;
    }

    /**
     * Loads the robot configuration from a JSON file on the classpath.
     *
     * @param resourcePath the path to the JSON configuration file (e.g., "/json/robot_config.json")
     * @return a {@code RobotConfig} instance populated with values from the file
     * @throws IOException if reading or parsing the file fails
     */
    public static RobotConfig load(String resourcePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream in = RobotConfig.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return mapper.readValue(in, RobotConfig.class);
        }
    }
}
