/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

/**
 * Defines the types of demonstration scenarios available in the lv223 planetary
 * simulation. Each enum value specifies a distinct robot scenario or action set
 * on the planet.
 *
 * <p>The available scenarios include:</p>
 * <ul>
 *   <li>{@code NONE} - No demo scenario (default).</li>
 *   <li>{@code SEE} - The whole planet is visible.</li>
 *   <li>{@code MOVE} - Basic robot movement.</li>
 *   <li>{@code MOVE_AND_SCAN} - Movement combined with environmental scanning.</li>
 *   <li>{@code MOVE_AND_HARVEST} - Harvesting resources while moving.</li>
 *   <li>{@code MOVE_AND_PIPE} - Pipeline construction activity.</li>
 *   <li>{@code MOVE_AND_MINE} - Mineral extraction activity.</li>
 *   <li>{@code MOVE_AND_CULTIVATE} - Crop cultivation activity.</li>
 *   <li>{@code PUMP} - Water pumping scenario.</li>
 *   <li>{@code VARIOUS} - Mixed or various activities.</li>
 * </ul>
 *
 * <p>This enum also provides a {@code fromString} method for converting string representations
 * into enum values, and a {@code toSimplifiedString} method to obtain a concise representation.
 * These are useful for configuration parsing and logging.</p>
 *
 * @since 1.0
 */
public enum RobotScenarioType {
    NONE,
    SEE,
    MOVE,
    MOVE_AND_SCAN,
    MOVE_AND_HARVEST,
    MOVE_AND_PIPE,
    MOVE_AND_MINE,
    MOVE_AND_CULTIVATE,
    PUMP,
    VARIOUS;

    /**
     * Converts a string representation of a demo scenario to its corresponding enum value.
     * The conversion is case-insensitive.
     *
     * @param demoTypeStr the string representation of the demo scenario
     * @return the corresponding {@code RobotScenarioType} enum value; returns {@code NONE} if no match is found
     */
    public static RobotScenarioType fromString(String demoTypeStr) {
        if (demoTypeStr == null) {
            return NONE;
        }
        switch (demoTypeStr.toLowerCase()) {
            case "see":
                return SEE;
            case "move":
                return MOVE;
            case "scan":
                return MOVE_AND_SCAN;
            case "cultivate":
                return MOVE_AND_CULTIVATE;
            case "harvest":
                return MOVE_AND_HARVEST;
            case "pipe":
                return MOVE_AND_PIPE;
            case "mine":
                return MOVE_AND_MINE;
            case "pump":
                return PUMP;
            case "var":
                return VARIOUS;
            case "none":
            default:
                return NONE;
        }
    }

    /**
     * Returns a simplified string representation of the given demo scenario.
     *
     * @param demoType the demo scenario
     * @return a simplified string representation (e.g., "scan" for {@code MOVE_AND_SCAN})
     */
    public static String toSimplifiedString(RobotScenarioType demoType) {
        if (demoType == null) {
            return "none";
        }
        switch (demoType) {
            case SEE:
                return "see";
            case MOVE:
                return "move";
            case MOVE_AND_SCAN:
                return "scan";
            case MOVE_AND_CULTIVATE:
                return "cultivate";
            case MOVE_AND_HARVEST:
                return "harvest";
            case MOVE_AND_PIPE:
                return "pipe";
            case MOVE_AND_MINE:
                return "mine";
            case PUMP:
                return "pump";
            case VARIOUS:
                return "var";
            case NONE:
            default:
                return "none";
        }
    }
}
