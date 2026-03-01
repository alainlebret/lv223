/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

/**
 * Enum representing the different types of requests supported by the planet server.
 *
 * <p>This enum defines constant values for each supported request category that
 * can be sent to the planet server, such as scanning, moving, cultivating, harvesting,
 * piping, pumping, and mining.</p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public enum RequestType {
    SCAN,
    MOVE,
    CULTIVATE,
    HARVEST,
    PIPE,
    PUMP,
    MINE
}
