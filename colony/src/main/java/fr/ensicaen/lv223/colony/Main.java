/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main entry point for the colony-side simulation.
 * <p>
 * This class loads the robot configuration, establishes a connection with the planet server,
 * initializes the environment facade and robots, and then starts the simulation loop.
 * </p>
 *
 * @since 1.0
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Determine server address and port from command-line arguments (with defaults)
        try {
            String serverAddress = args.length > 0 ? args[0] : "localhost";
            int port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
    
            logger.info("Creating a new ColonyManager using server address {} and port {}.", serverAddress, port);
            ColonyManager manager = new ColonyManager(serverAddress, port);
            logger.debug("Starting colony simulation...");
            manager.runSimulation();
        } catch (NumberFormatException e) {
            logger.error("Invalid port number provided. Please provide a valid port.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
        }
    }
}
