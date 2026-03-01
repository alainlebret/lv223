/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

/**
 * Enumerates the types of actions that can be performed by a robot in the colony simulation.
 * <p>
 * Each constant represents a specific action that may be requested from the server
 * or processed by the client. Additional action types can be added as needed.
 * </p>
 */
public enum ActionType {
    SCAN,
    MOVE,
    HARVEST,
    MINE,
    CULTIVATE,
    PIPE,
    PUMP
    // Additional actions can be defined here.
}
