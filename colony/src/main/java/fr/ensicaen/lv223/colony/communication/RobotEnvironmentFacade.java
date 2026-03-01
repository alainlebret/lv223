/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.pojo.AffectedRobot;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.utils.Direction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Facilitates interaction between robots and the planet server.
 * <p>
 * This facade serves as the single point of entry for all robot actions (e.g., scan, move, harvest)
 * and dispatches JSON requests to the server as well as processing the corresponding responses.
 * It also manages the translation between local and global coordinates based on the colony base.
 * </p>
 */
public class RobotEnvironmentFacade {
    private static final Logger logger = LogManager.getLogger(RobotEnvironmentFacade.class);

    /** The connection to the planet server */
    private final PlanetServerConnection serverConnection;

    /** Observers subscribed to receive environment feedback */
    private final Map<String, Robot> robotObservers;

    /** Global X coordinate of the colony base */
    private final int baseX;

    /** Global Y coordinate of the colony base */
    private final int baseY;

    /** Handler for processing action responses from the server */
    private final ActionResponseHandler actionResponseHandler;

    /** Dispatcher for routing responses to the correct handler */
    private final ResponseDispatcher responseDispatcher;

    /**
     * Constructs a new RobotEnvironmentFacade.
     *
     * @param connection the server connection for sending requests and receiving responses
     * @param baseX      the global X coordinate of the colony base
     * @param baseY      the global Y coordinate of the colony base
     */
    public RobotEnvironmentFacade(PlanetServerConnection connection, int baseX, int baseY) {
        this.serverConnection = connection;
        this.robotObservers = new HashMap<>();
        this.baseX = baseX;
        this.baseY = baseY;
        this.actionResponseHandler = new ActionResponseHandler();
        this.responseDispatcher = new ResponseDispatcher(baseX, baseY);
    }

    /**
     * Subscribes a robot to receive environment feedback.
     *
     * @param robot     the robot to subscribe
     * @param robotName the name of the robot
     */
    public void subscribe(Robot robot, String robotName) {
        if (robot != null && robotName != null && !robotName.isEmpty()) {
            robotObservers.put(robotName, robot);
        } else {
            logger.warn("Attempted to subscribe an observer with a null or empty robot name.");
        }
    }

    /**
     * Notifies all subscribed robots with the provided environment feedback.
     *
     * @param feedback the environment feedback to send to the robots
     */
    public void notifyObservers(EnvironmentFeedback feedback) {
        if (feedback == null) {
            logger.warn("Null feedback provided; skipping notification.");
            return;
        }
        for (Robot robot : robotObservers.values()) {
            robot.update(feedback);
        }
    }

    /**
     * Processes a server response and notifies any robots that sustained injuries.
     * <p>
     * Robots with a non-zero injury value in {@code affectedRobots} are collected and
     * delivered to all subscribers as an {@link EnvironmentFeedback#injuredRobots} event,
     * which triggers {@link fr.ensicaen.lv223.colony.manager.HealthManager#update} via
     * each robot's {@code update()} callback.
     * </p>
     *
     * @param response the raw JSON response from the server
     */
    public void processServerResponse(String response) {
        ActionResponse parsed = actionResponseHandler.parseResponse(response);
        if (parsed == null || parsed.getAffectedRobots() == null) {
            return;
        }
        List<Robot> injuredRobots = new ArrayList<>();
        for (AffectedRobot affected : parsed.getAffectedRobots()) {
            if (affected.getInjury() > 0) {
                Robot r = robotObservers.get(affected.getId());
                if (r != null) {
                    injuredRobots.add(r);
                }
            }
        }
        if (!injuredRobots.isEmpty()) {
            notifyObservers(EnvironmentFeedback.injuredRobots(injuredRobots));
        }
    }

    /**
     * Sends a scan request for the specified robot.
     *
     * @param robot the robot that will perform the scan
     */
    public void scan(Robot robot) {
        if (robot == null) {
            logger.error("Robot cannot be null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildScanRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY());
        } catch (JsonProcessingException e) {
            logger.error("Error building scan request: {}", e.getMessage());
            return;
        }
        if (request == null || !request.contains(ActionType.SCAN.toString().toLowerCase())) {
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during scan: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a move request for the specified robot in the given direction.
     *
     * @param robot     the robot to move
     * @param direction the direction to move the robot
     */
    public void moveRobot(Robot robot, Direction direction) {
        if (robot == null || direction == null) {
            logger.error("Invalid arguments for moving robot");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        Coordinate targetGlobal = translateToGlobalDirection(currentGlobal, direction);
        String request;
        try {
            request = RequestBuilder.buildMoveRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY(),
                    targetGlobal.getX(), targetGlobal.getY());
        } catch (JsonProcessingException e) {
            logger.error("Error building move request: {}", e.getMessage());
            return;
        }
        if (request == null || !request.contains(ActionType.MOVE.toString().toLowerCase())) {
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, targetGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during move: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a harvest request for the specified robot.
     *
     * @param robot the robot that will perform harvesting
     * @param units the number of units to harvest
     */
    public void harvest(Robot robot, int units) {
        if (robot == null) {
            logger.error("Cannot perform harvest: robot is null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildHarvestRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY(), units);
        } catch (JsonProcessingException e) {
            logger.error("Error building harvest request: {}", e.getMessage());
            return;
        }
        String expected = "\"" + ActionType.HARVEST.toString().toLowerCase() + "\"";
        if (request == null || !request.contains(expected)) {
            logger.error("Invalid harvest request: {}", request);
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during harvest: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a cultivate request for the specified robot.
     *
     * @param robot the robot that will perform cultivation
     * @param units the number of units to cultivate
     */
    public void cultivate(Robot robot, int units) {
        if (robot == null) {
            logger.error("Cannot perform cultivate: robot is null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildCultivateRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY(), units);
        } catch (JsonProcessingException e) {
            logger.error("Error building cultivate request: {}", e.getMessage());
            return;
        }
        String expected = "\"" + ActionType.CULTIVATE.toString().toLowerCase() + "\"";
        if (request == null || !request.contains(expected)) {
            logger.error("Invalid cultivate request: {}", request);
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during cultivate: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a mine request for the specified robot.
     *
     * @param robot the robot that will perform the mining
     * @param units the number of mineral units to extract
     */
    public void mine(Robot robot, int units) {
        if (robot == null) {
            logger.error("Cannot perform mine: robot is null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildMineRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY(), units);
        } catch (JsonProcessingException e) {
            logger.error("Error building mine request: {}", e.getMessage());
            return;
        }
        String expected = "\"" + ActionType.MINE.toString().toLowerCase() + "\"";
        if (request == null || !request.contains(expected)) {
            logger.error("Invalid mine request: {}", request);
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during mine: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a pipe construction request for the specified robot.
     *
     * @param robot the robot that will construct the pipeline segment
     */
    public void pipe(Robot robot) {
        if (robot == null) {
            logger.error("Cannot perform pipe: robot is null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildPipeRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY());
        } catch (JsonProcessingException e) {
            logger.error("Error building pipe request: {}", e.getMessage());
            return;
        }
        if (request == null || !request.contains(ActionType.PIPE.toString().toLowerCase())) {
            logger.error("Invalid pipe request: {}", request);
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during pipe: {}", e.getMessage(), e);
        }
    }

    /**
     * Sends a pump request for the specified robot.
     * <p>
     * The robot must be positioned on a water-bearing cell that is connected to
     * an operational pipeline. The server decrements the cell's water units by
     * the requested amount.
     * </p>
     *
     * @param robot the robot that will perform the pumping
     * @param units the number of water units to extract
     */
    public void pump(Robot robot, int units) {
        if (robot == null) {
            logger.error("Cannot perform pump: robot is null");
            return;
        }
        Coordinate currentGlobal = translateToGlobal(robot.getCurrentLocation());
        String request;
        try {
            request = RequestBuilder.buildPumpRequest(robot.getName(), robot.getType().toFormattedString(),
                    currentGlobal.getX(), currentGlobal.getY(), units);
        } catch (JsonProcessingException e) {
            logger.error("Error building pump request: {}", e.getMessage());
            return;
        }
        String expected = "\"" + ActionType.PUMP.toString().toLowerCase() + "\"";
        if (request == null || !request.contains(expected)) {
            logger.error("Invalid pump request: {}", request);
            return;
        }
        try {
            String responseJson = serverConnection.sendRequest(request);
            if (responseJson != null) {
                ActionResponse response = actionResponseHandler.parseResponse(responseJson);
                responseDispatcher.dispatch(response, robot, currentGlobal);
                processServerResponse(responseJson);
            }
        } catch (Exception e) {
            logger.error("Error during pump: {}", e.getMessage(), e);
        }
    }

    // --- Helper Methods for Coordinate Translation ---

    /**
     * Converts a local coordinate to a global coordinate.
     *
     * @param local the local coordinate to convert
     * @return the corresponding global coordinate
     */
    private Coordinate translateToGlobal(Coordinate local) {
        return new Coordinate(baseX + local.getX(), baseY + local.getY());
    }

    /**
     * Converts a global coordinate to a local coordinate using the provided base values.
     *
     * @param global the global coordinate to convert
     * @param baseX  the global X coordinate of the colony base
     * @param baseY  the global Y coordinate of the colony base
     * @return the corresponding local coordinate
     */
    public static Coordinate translateToLocalStatic(Coordinate global, int baseX, int baseY) {
        return new Coordinate(global.getX() - baseX, global.getY() - baseY);
    }

    /**
     * Computes a target global coordinate by applying a direction offset to a given global coordinate.
     *
     * @param global    the current global coordinate
     * @param direction the direction in which to move
     * @return the new global coordinate after applying the direction offset
     */
    private Coordinate translateToGlobalDirection(Coordinate global, Direction direction) {
        return new Coordinate(global.getX() + direction.getDeltaX(), global.getY() + direction.getDeltaY());
    }

    /**
     * Gets the global X coordinate of the colony base.
     *
     * @return the base X coordinate
     */
    public int getBaseX() {
        return baseX;
    }

    /**
     * Gets the global Y coordinate of the colony base.
     *
     * @return the base Y coordinate
     */
    public int getBaseY() {
        return baseY;
    }
}
