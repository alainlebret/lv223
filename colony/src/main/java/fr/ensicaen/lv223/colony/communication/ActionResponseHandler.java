/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.utils.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the server's response to an action request.
 * <p>
 * This class provides methods to parse a JSON response into an
 * {@link ActionResponse} object and to process the parsed response.
 * </p>
 */
public class ActionResponseHandler {
    private static final Logger logger = LogManager.getLogger(ActionResponseHandler.class);
    private final ObjectMapper objectMapper = JsonUtils.getMapper();

    /**
     * Parses the JSON response into an ActionResponse object.
     *
     * @param jsonResponse the JSON string received from the server
     * @return the parsed ActionResponse, or null if parsing fails
     */
    public ActionResponse parseResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, ActionResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing action response: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Processes the parsed response.
     * <p>
     * This method logs the response's status and message. It can be extended to
     * perform additional actions based on the response content.
     * </p>
     *
     * @param response the parsed ActionResponse
     */
    public void processResponse(ActionResponse response) {
        if (response == null) {
            logger.error("No response to process.");
            return;
        }
        logger.info("Response status: {}", response.getStatus());
        logger.info("Response message: {}", response.getMessage());
        // Additional processing based on the response can be added here.
    }
}
