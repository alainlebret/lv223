/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Defines the contract for handling requests from clients connected to the planet server.
 *
 * <p>This interface should be implemented by classes that process incoming client requests,
 * converting them into appropriate actions and generating a corresponding JSON response.</p>
 *
 * @since 1.0
 */
public interface RequestHandler {

    /**
     * Processes a request from a client encapsulated in a {@code RequestContext}.
     *
     * <p>The implementation should validate the request, perform the required action,
     * and return a JSON response string indicating the result (success or error).</p>
     *
     * @param context the request context containing the client request and server reference
     * @return a JSON string representing the response to the client
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
    String handleRequest(RequestContext context) throws JsonProcessingException;
}