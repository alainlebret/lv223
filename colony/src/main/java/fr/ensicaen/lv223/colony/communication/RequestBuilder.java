/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds JSON requests for various actions.
 * <p>
 * This utility class provides static methods to construct JSON-formatted
 * action requests. It delegates the JSON serialization to {@link ActionRequestFactory}.
 * </p>
 */
public final class RequestBuilder {

    // Private constructor to prevent instantiation.
    private RequestBuilder() {}

    /**
     * Builds a JSON scan request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate
     * @param y         the global y-coordinate
     * @return a JSON string representing a scan request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildScanRequest(String robotId, String robotType, int x, int y)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        return ActionRequestFactory.createActionRequest(
                ActionType.SCAN.toString().toLowerCase(), robotId, robotType, parameters);
    }

    /**
     * Builds a JSON move request.
     *
     * @param robotId         the robot's identifier
     * @param robotType       the robot's type
     * @param currentGlobalX  the current global x-coordinate
     * @param currentGlobalY  the current global y-coordinate
     * @param newGlobalX      the new global x-coordinate after moving
     * @param newGlobalY      the new global y-coordinate after moving
     * @return a JSON string representing a move request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildMoveRequest(String robotId, String robotType,
                                          int currentGlobalX, int currentGlobalY,
                                          int newGlobalX, int newGlobalY)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", currentGlobalX);
        parameters.put("y", currentGlobalY);
        parameters.put("newX", newGlobalX);
        parameters.put("newY", newGlobalY);
        return ActionRequestFactory.createActionRequest(
                ActionType.MOVE.toString().toLowerCase(), robotId, robotType, parameters);
    }

    /**
     * Builds a JSON harvest request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate
     * @param y         the global y-coordinate
     * @param units     the number of units to harvest
     * @return a JSON string representing a harvest request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildHarvestRequest(String robotId, String robotType, int x, int y, int units)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        parameters.put("units", units);
        return ActionRequestFactory.createActionRequest(
                ActionType.HARVEST.toString().toLowerCase(), robotId, robotType, parameters);
    }

    /**
     * Builds a JSON cultivate request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate
     * @param y         the global y-coordinate
     * @param units     the number of units to cultivate
     * @return a JSON string representing a cultivate request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildCultivateRequest(String robotId, String robotType, int x, int y, int units)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        parameters.put("units", units);
        return ActionRequestFactory.createActionRequest(
                ActionType.CULTIVATE.toString().toLowerCase(), robotId, robotType, parameters);
    }
    
    /**
     * Builds a JSON mine request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate of the target cell
     * @param y         the global y-coordinate of the target cell
     * @param units     the number of mineral units to extract
     * @return a JSON string representing a mine request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildMineRequest(String robotId, String robotType, int x, int y, int units)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        parameters.put("units", units);
        return ActionRequestFactory.createActionRequest(
                ActionType.MINE.toString().toLowerCase(), robotId, robotType, parameters);
    }

    /**
     * Builds a JSON pump request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate of the water cell
     * @param y         the global y-coordinate of the water cell
     * @param units     the number of water units to pump
     * @return a JSON string representing a pump request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildPumpRequest(String robotId, String robotType, int x, int y, int units)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        parameters.put("units", units);
        return ActionRequestFactory.createActionRequest(
                ActionType.PUMP.toString().toLowerCase(), robotId, robotType, parameters);
    }

    /**
     * Builds a JSON pipe request.
     *
     * @param robotId   the robot's identifier
     * @param robotType the robot's type
     * @param x         the global x-coordinate of the target cell
     * @param y         the global y-coordinate of the target cell
     * @return a JSON string representing a pipe request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String buildPipeRequest(String robotId, String robotType, int x, int y)
            throws JsonProcessingException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        return ActionRequestFactory.createActionRequest(
                ActionType.PIPE.toString().toLowerCase(), robotId, robotType, parameters);
    }
}
