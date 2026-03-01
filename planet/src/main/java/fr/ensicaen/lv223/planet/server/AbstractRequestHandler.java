/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.pojo.ActionResponse;
import fr.ensicaen.lv223.planet.pojo.AffectedRobot;
import fr.ensicaen.lv223.planet.pojo.DetectedCell;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;
import fr.ensicaen.lv223.planet.utils.JsonUtils;

/**
 * Abstract base class for request handlers.
 * <p>
 * This class provides common validation and response construction logic for handling
 * action requests. Specific request handling is delegated to concrete subclasses via
 * the abstract method {@link #handleSpecificRequest(RequestContext, ActionRequest, ActionParameters, int, int)}.
 * </p>
 */
public abstract class AbstractRequestHandler implements RequestHandler {

    private static final ObjectMapper MAPPER = JsonUtils.getMapper();

    @Override
    public String handleRequest(RequestContext context) throws JsonProcessingException {
        ActionRequest request = context.getActionRequest();
        ActionParameters params = extractActionParameters(request);

        // Perform common validations.
        if (!isValidRobotType(RobotType.fromString(request.getRobotType()))) {
            return createErrorResponse(request, ResponseType.INVALID_ROBOT_TYPE);
        }

        int x = params.getX();
        int y = params.getY();
        if (context.getServer().isInvalidCell(x, y)) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        // Delegate to specific handler logic.
        return handleSpecificRequest(context, request, params, x, y);
    }

    /**
     * Handles the request after common validations have been performed.
     *
     * @param context the request context
     * @param request the action request
     * @param params  the extracted action parameters
     * @param x       the x-coordinate provided in the parameters
     * @param y       the y-coordinate provided in the parameters
     * @return a JSON string representing the response
     * @throws JsonProcessingException if JSON processing fails
     */
    protected abstract String handleSpecificRequest(RequestContext context, ActionRequest request,
            ActionParameters params, int x, int y) throws JsonProcessingException;

    /**
     * Validates whether the provided robot type is acceptable.
     *
     * @param robotType the robot type to validate
     * @return {@code true} if the robot type is valid; {@code false} otherwise
     */
    protected abstract boolean isValidRobotType(RobotType robotType);

    /**
     * Extracts action parameters from the action request.
     * <p>
     * Returns a default (zero-valued) {@link ActionParameters} instance when the
     * request carries no {@code "parameters"} field, so that handlers can still
     * apply their own validation rather than receiving a {@code null} object.
     * </p>
     *
     * @param request the action request
     * @return an {@code ActionParameters} object, never {@code null}
     */
    protected ActionParameters extractActionParameters(ActionRequest request) {
        if (request.getParameters() == null) {
            return new ActionParameters();
        }
        return MAPPER.convertValue(request.getParameters(), ActionParameters.class);
    }

    /**
     * Constructs a list of affected robots based on the request context and a list of impacted robot IDs.
     *
     * @param context            the request context
     * @param request            the action request
     * @param impactedRobotIds   the list of impacted robot IDs
     * @return a list of {@code AffectedRobot} objects
     */
    protected List<AffectedRobot> getAffectedRobots(RequestContext context, ActionRequest request,
            List<Integer> impactedRobotIds) {
        List<AffectedRobot> affectedRobots = new ArrayList<>();
        // Add the primary robot involved in the request.
        affectedRobots.add(new AffectedRobot(request.getRobotId(), request.getRobotType(), 0));
        // Add any robots affected by metamorphoses.
        for (Integer robotId : impactedRobotIds) {
            RobotInfo robotInfo = context.getServer().getColonyRobots().get(robotId.toString());
            if (robotInfo != null) {
                affectedRobots.add(new AffectedRobot(robotInfo.getId(), robotInfo.getType().toString(), robotInfo.getInjury()));
            }
        }
        return affectedRobots;
    }

    /**
     * Creates a JSON error response for the specified request and response type.
     *
     * @param request      the action request
     * @param responseType the type of error response
     * @return a JSON string representing the error response
     * @throws JsonProcessingException if JSON processing fails
     */
    protected String createErrorResponse(ActionRequest request, ResponseType responseType)
            throws JsonProcessingException {
        ActionResponse response = new ActionResponse();
        response.setStatus("error");
        response.setAction(request.getAction());
        response.setAffectedRobots(Collections.singletonList(
                new AffectedRobot(request.getRobotId(), request.getRobotType(), 0)
        ));
        // Ensure detectedCells is not null to avoid NullPointerException.
        response.setDetectedCells(new ArrayList<>());
        response.setMessage(formatResponseMessage(responseType.toString()));
        return MAPPER.writeValueAsString(response);
    }

    /**
     * Creates a JSON success response for the specified request.
     *
     * @param request       the action request
     * @param detectedCells the list of detected cells (may be empty)
     * @param affectedRobots the list of affected robots
     * @return a JSON string representing the success response
     * @throws JsonProcessingException if JSON processing fails
     */
    protected String createSuccessResponse(ActionRequest request, List<DetectedCell> detectedCells,
            List<AffectedRobot> affectedRobots) throws JsonProcessingException {
        ActionResponse response = new ActionResponse();
        response.setStatus("success");
        response.setAction(request.getAction());
        response.setAffectedRobots(affectedRobots);

        // If detectedCells is null, set it to an empty list.
        if (detectedCells == null) {
            detectedCells = new ArrayList<>();
        }
        response.setDetectedCells(detectedCells);
        return MAPPER.writeValueAsString(response);
    }

    /**
     * Formats a response message by splitting on underscores and converting the parts to a human-readable form.
     *
     * @param message the original message string
     * @return the formatted message string
     */
    private String formatResponseMessage(String message) {
        String[] parts = message.split("_");
        String formattedString = parts[0].substring(0, 1) + parts[0].substring(1).toLowerCase();

        for (int i = 1; i < parts.length; i++) {
            formattedString += " " + parts[i].toLowerCase();
        }
        return formattedString;
    }
}
