/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

/**
 * Enumeration of robot types used in the colony.
 * <p>
 * Defines constant values for the different robot categories available for performing colony tasks.
 *
 * @since 1.0
 */
public enum RobotType {
    UNKNOWN,
    CENTRALIZER,
    PIPELINER,
    FARMER,
    HARVESTER,
    CARTOGRAPHER,
    MINER;

    /**
     * Returns a formatted string representation of the RobotType.
     * The string is formatted by capitalizing the first letter and making the remaining letters lowercase.
     *
     * @return the formatted string representation of the RobotType
     */
    public String toFormattedString() {
        String name = this.name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Converts a string value to a {@code RobotType} enum.
     *
     * @param value the string value to convert
     * @return the corresponding {@code RobotType} enum value, or {@code UNKNOWN} if no match is found or if {@code value} is null
     */
    public static RobotType fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        try {
            return RobotType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
