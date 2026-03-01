/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

/**
 * Enum representing the types of clients connecting to the planet server.
 * <p>
 * This enumeration defines constant values for different client categories,
 * such as the graphical user interface (GUI) client and the robot colony client.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public enum ClientType {
    GUI_CLIENT,
    COLONY_CLIENT
}
