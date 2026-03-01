/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.ensicaen.lv223.colony.pojo.ActionRequest;
import fr.ensicaen.lv223.colony.utils.JsonUtils;
import java.util.Map;

/**
 * Factory for creating JSON action request strings.
 * <p>
 * This class provides a static method to build a JSON string that represents
 * an action request. The factory uses a shared ObjectMapper from {@link JsonUtils}
 * to perform the serialization.
 * </p>
 */
public final class ActionRequestFactory {

    // Private constructor to prevent instantiation.
    private ActionRequestFactory() {}

    /**
     * Creates a JSON string representing an action request.
     *
     * @param action     the action name (e.g., "harvest", "move", etc.)
     * @param robotId    the robot's identifier
     * @param robotType  the robot's type
     * @param parameters a map of parameters for the action; may be empty but should not be null.
     * @return a JSON string representing the action request
     * @throws JsonProcessingException if JSON processing fails
     */
    public static String createActionRequest(String action, String robotId, String robotType, Map<String, Object> parameters)
            throws JsonProcessingException {
        // Create a new action request and set its properties.
        ActionRequest request = new ActionRequest();
        request.setAction(action);
        request.setRobotId(robotId);
        request.setRobotType(robotType);
        request.setParameters(parameters);
        // Serialize the request into JSON.
        return JsonUtils.getMapper().writeValueAsString(request);
    }
}
