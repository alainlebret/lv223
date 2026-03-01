/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

/**
 * Enum representing different response types for requests sent to the planet server.
 *
 * <p>This enum defines constant values that indicate the outcome of a request.
 * For example, it distinguishes between successful requests and various error conditions
 * such as invalid input, non-adjacency, or missing pipeline connections.</p>
 *
 * @since 1.0
 */
public enum ResponseType {
    INVALID_REQUEST,
    INVALID_ROBOT_TYPE,
    INVALID_CELL,
    INVALID_DESTINATION_CELL,
    INVALID_QUANTITY,
    SUCCESS,
    ERROR,
    NON_ADJACENT_CELL,
    NO_PIPELINE_CONNECTION
}
