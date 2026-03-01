/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.colony.communication.ConnectionClosedException;
import fr.ensicaen.lv223.colony.communication.PlanetServerConnection;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.config.RobotConfig;
import fr.ensicaen.lv223.colony.decision.LocalMap;
import fr.ensicaen.lv223.colony.robot.Cartographer;
import fr.ensicaen.lv223.colony.robot.Centralizer;
import fr.ensicaen.lv223.colony.robot.Farmer;
import fr.ensicaen.lv223.colony.robot.Harvester;
import fr.ensicaen.lv223.colony.robot.Miner;
import fr.ensicaen.lv223.colony.robot.Pipeliner;
import fr.ensicaen.lv223.colony.robot.Robot;

/**
 * Manages the robot colony by maintaining an active robot list and coordinating
 * communication with the planet server.
 * <p>
 * The ColonyManager is responsible for:
 * <ul>
 *   <li>Establishing a connection to the planet server.</li>
 *   <li>Creating the communication facade.</li>
 *   <li>Running the simulation loop where each active robot performs its task.</li>
 *   <li>Sending end-of-turn signals and handling shutdown conditions.</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     ColonyManager manager = new ColonyManager("localhost", 12345);
 *     manager.runSimulation();
 * </pre>
 * </p>
 *
 * @since 1.0
 */
public class ColonyManager {
    private static final Logger logger = LogManager.getLogger(ColonyManager.class);

    private PlanetServerConnection serverConnection;
    private RobotEnvironmentFacade facade;
    private List<Robot> activeRobots;
    private Centralizer centralizer;
    private volatile boolean running = true;

    public static final int DAYS_PER_YEAR = 364;

    private static final String CONFIG_PATH = "/json/robot_config.json";

    /**
     * Constructs a ColonyManager by establishing a connection to the planet server.
     * <p>
     * Colony base coordinates and robot parameters are read from
     * {@code /json/robot_config.json} on the classpath. If the connection
     * cannot be established, an {@code IllegalStateException} is thrown.
     * </p>
     *
     * @param serverAddress the address of the planet server
     * @param port          the port of the planet server
     * @throws IllegalStateException if connection to the server fails or config cannot be loaded
     */
    public ColonyManager(String serverAddress, int port) {
        RobotConfig config;
        try {
            config = RobotConfig.load(CONFIG_PATH);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load colony configuration from " + CONFIG_PATH, e);
        }

        serverConnection = new PlanetServerConnection(serverAddress, port);
        if (!serverConnection.isConnected()) {
            String errorMsg = String.format("Unable to connect to the server at %s:%d. Exiting.", serverAddress, port);
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        facade = new RobotEnvironmentFacade(serverConnection, config.getBaseX(), config.getBaseY());
        logger.info("Colony base set to ({}, {}).", config.getBaseX(), config.getBaseY());
        activeRobots = new ArrayList<>();

        // For demonstration purposes, add some robots
        // Note: Uncomment additional robots as needed.
        Cartographer cartographer1 = new Cartographer(facade);
        Harvester harvester1 = new Harvester(facade);
        Farmer farmer1 = new Farmer(facade);
        addRobot(cartographer1);
        addRobot(harvester1);
        addRobot(farmer1);

        // The Centralizer is not a robot: it stays at the base, has no sensors or
        // effectors, and coordinates the active fleet by reading their state each turn.
        // However, the Centralizer may be considered as an advanced cognitive AI.
        centralizer = new Centralizer(activeRobots);
    }

    /**
     * Adds a robot to the active robot list.
     *
     * @param robot the robot to add
     */
    public void addRobot(Robot robot) {
        activeRobots.add(robot);
    }

    /**
     * Removes a robot from the active robot list.
     *
     * @param robot the robot to remove
     */
    public void removeRobot(Robot robot) {
        activeRobots.remove(robot);
    }

    /**
     * Runs the colony simulation.
     * <p>
     * The simulation loop is executed for a fixed number of turns.
     * In each turn:
     * <ol>
     *   <li>Every active robot performs its task via {@code performTask()}.</li>
     *   <li>An end-of-turn message is sent to the planet server.</li>
     *   <li>The manager waits for the server's confirmation.</li>
     *   <li>A short delay is introduced to allow synchronization.</li>
     * </ol>
     * </p>
     *
     * @see PlanetServerConnection#sendEndOfTurnMessage()
     * @see PlanetServerConnection#waitForTurnCompletion()
     * @see PlanetServerConnection#resetTurnCompletion()
     */
    public void runSimulation() {
        int totalTurns = 2 * DAYS_PER_YEAR;
        
        for (int turn = 0; running && turn < totalTurns; turn++) {
            logger.debug("Simulation turn {} of {}", turn + 1, totalTurns);
            
            // Let the centralizer coordinate before robots act.
            centralizer.coordinate();

            // Let each active robot perform its task (iterate a snapshot to avoid ConcurrentModificationException).
            try {
                new ArrayList<>(activeRobots).forEach(Robot::performTask);
            } catch (ConnectionClosedException e) {
                logger.warn("Connection lost during robot tasks: {}", e.getMessage());
                break;
            }
    
            // Send end-of-turn signal to the server.
            logger.debug("Sending end-of-turn message.");
            serverConnection.sendEndOfTurnMessage();
            serverConnection.waitForTurnCompletion();
    
            // Check connection status.
            if (!serverConnection.isConnected()) {
                logger.warn("Server connection lost. Terminating simulation loop.");
                break;
            }
            if (serverConnection.isShutdownRequested()) {
                logger.warn("Server has requested shutdown. Terminating simulation loop.");
                break;
            }
    
            serverConnection.resetTurnCompletion();
            
            try {
                Thread.sleep(1000); // Pause between turns (1 second)
            } catch (InterruptedException e) {
                logger.error("Sleep interrupted during simulation.", e);
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Simulation complete or shutdown requested. Closing connection.");
        serverConnection.close();
    }
    
    /**
     * Shuts down the simulation.
     */
    public void shutdown() {
        logger.info("Shutdown requested on colony client.");
        running = false;
    }

    /**
     * Displays the neighborhood of the colony's local map.
     * <p>
     * This method logs detailed information for each cell in the local map,
     * which can be useful for debugging and analysis.
     * </p>
     *
     * @param localMap the colony's local map
     */
    private void displayNeighborhood(LocalMap localMap) {
        localMap.getMap().forEach((coord, cell) -> 
            logger.info("Cell at {}: Type: {}, Data: {}", coord, cell.getType(), cell.getData())
        );
    }
}
