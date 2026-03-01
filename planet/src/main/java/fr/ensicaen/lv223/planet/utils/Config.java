/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class used to store planet simulation parameters.
 * <p>
 * This class holds default and configurable parameters such as turn delay,
 * total simulation years, server port, the path to the planet configuration file,
 * and the simulation scenario.
 * </p>
 */
public class Config {
    public static final int DEFAULT_DELAY_MILLISECONDS = 1000;
    public static final int DEFAULT_NUMBER_OF_YEARS = 8;
    public static final int DEFAULT_PORT = 12345;
    public static final Path DEFAULT_JSON_PATH = Paths.get("target", "classes", "json", "planet2.json");
    public static final RobotScenarioType DEFAULT_SCENARIO = RobotScenarioType.NONE;

    private int turnDelayMilliseconds;
    private int totalNumberOfYears;
    private int port;
    private Path configPlanetFilePath;
    private RobotScenarioType scenario;

    /**
     * Constructs a new Config with default values.
     */
    public Config() {
        this.turnDelayMilliseconds = DEFAULT_DELAY_MILLISECONDS;
        this.totalNumberOfYears = DEFAULT_NUMBER_OF_YEARS;
        this.port = DEFAULT_PORT;
        this.configPlanetFilePath = DEFAULT_JSON_PATH;
        this.scenario = DEFAULT_SCENARIO;
    }

    /**
     * Returns the delay in milliseconds between simulation turns.
     *
     * @return the turn delay in milliseconds.
     */
    public int getTurnDelayMilliseconds() {
        return turnDelayMilliseconds;
    }

    /**
     * Sets the delay in milliseconds between simulation turns.
     *
     * @param turnDelayMilliseconds the turn delay in milliseconds.
     */
    public void setTurnDelayMilliseconds(int turnDelayMilliseconds) {
        this.turnDelayMilliseconds = turnDelayMilliseconds;
    }

    /**
     * Returns the total number of simulation years.
     *
     * @return the total number of years.
     */
    public int getTotalNumberOfYears() {
        return totalNumberOfYears;
    }

    /**
     * Sets the total number of simulation years.
     *
     * @param totalNumberOfYears the total number of years.
     */
    public void setTotalNumberOfYears(int totalNumberOfYears) {
        this.totalNumberOfYears = totalNumberOfYears;
    }

    /**
     * Returns the server port number.
     *
     * @return the port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the server port number.
     *
     * @param port the port number.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the simulation scenario.
     *
     * @return the simulation scenario.
     */
    public RobotScenarioType getScenario() {
        return scenario;
    }

    /**
     * Sets the simulation scenario.
     *
     * @param scenario the simulation scenario.
     */
    public void setScenario(RobotScenarioType scenario) {
        this.scenario = scenario;
    }

    /**
     * Returns the file path of the planet configuration JSON file.
     *
     * @return the configuration file path.
     */
    public Path getConfigPlanetFilePath() {
        return configPlanetFilePath;
    }

    /**
     * Sets the file path of the planet configuration JSON file.
     *
     * @param configPlanetFilePath the configuration file path.
     */
    public void setConfigPlanetFilePath(Path configPlanetFilePath) {
        this.configPlanetFilePath = configPlanetFilePath;
    }

    /**
     * Returns a string representation of this configuration.
     *
     * @return a string containing configuration details.
     */
    @Override
    public String toString() {
        return "Config [turnDelayMilliseconds=" + turnDelayMilliseconds +
                ", totalNumberOfYears=" + totalNumberOfYears +
                ", port=" + port +
                ", configPlanetFilePath=" + configPlanetFilePath +
                ", scenario=" + scenario + "]";
    }
}
