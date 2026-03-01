/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;

import fr.ensicaen.lv223.planet.server.PlanetServer;
import fr.ensicaen.lv223.planet.utils.Config;
import fr.ensicaen.lv223.planet.utils.JsonFileValidator;
import fr.ensicaen.lv223.planet.utils.RobotScenarioType;

/**
 * The Main class serves as the entry point for the planet server application.
 * It initializes and starts the {@code PlanetServer} with configurable parameters.
 * Command-line arguments are used to set up simulation parameters including:
 * <ul>
 *   <li>Simulation years</li>
 *   <li>Turn delay (milliseconds)</li>
 *   <li>Scenario type (demo mode)</li>
 *   <li>Server port</li>
 *   <li>Path to the JSON configuration file for the planet</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 *     java Main --years=1 --delay=1000 --scenario=move --port=12345 --config=target/classes/json/planet2.json
 * </pre>
 *
 * @version 1.2
 * @since 1.0
 */
@Parameters(separators = "=")
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final int FAILED = 1;
    private static final int ASKED_FOR_HELP = 2;
    private static final int SUCCEED = 0;

    @Parameter(names = { "--help", "-h" }, help = true, description = "Display help information")
    private boolean help = false;

    @Parameter(names = { "--years", "-y" }, description = "Number of years to simulate")
    public int years = Config.DEFAULT_NUMBER_OF_YEARS;

    @Parameter(names = { "--delay", "-d" }, description = "Delay between turns in milliseconds")
    private int delay = Config.DEFAULT_DELAY_MILLISECONDS;

    @Parameter(names = { "--scenario", "-s" }, description = "Type of scenario to run (see, move, scan, cultivate, harvest, pipe, mine, pump, var)")
    private String scenario = RobotScenarioType.toSimplifiedString(Config.DEFAULT_SCENARIO);

    @Parameter(names = { "--port", "-p" }, description = "Port number for the server")
    private int port = Config.DEFAULT_PORT;

    @Parameter(names = { "--config", "-c" }, required = false, validateWith = JsonFileValidator.class,
            converter = PathConverter.class, description = "Path to the JSON configuration file for the planet")
    private Path configPlanetFile = Config.DEFAULT_JSON_PATH;

    /**
     * Main entry point for the planet server application.
     *
     * @param argv Command-line arguments.
     */
    public static void main(String[] argv) {
        Main simulationMain = new Main();
        JCommander.newBuilder()
                .addObject(simulationMain)
                .build()
                .parse(argv);

        simulationMain.run();
    }

    /**
     * Initializes the configuration and starts the planet server.
     */
    private void run() {
        // Initialize simulation configuration
        Config simulationConfig = new Config();
        simulationConfig.setPort(port);
        simulationConfig.setTotalNumberOfYears(years);
        simulationConfig.setTurnDelayMilliseconds(delay);
        simulationConfig.setScenario(RobotScenarioType.fromString(scenario));
        simulationConfig.setConfigPlanetFilePath(configPlanetFile);
        logger.debug("Starting server with configuration: {}", simulationConfig);

        try {
            Planet planet = new Planet(simulationConfig);
            PlanetServer server = new PlanetServer(planet, simulationConfig);
            server.startServer();
        } catch (IOException e) {
            logger.error("Error starting server: {}", e.getMessage(), e);
            System.exit(FAILED);
        }
    }
}
