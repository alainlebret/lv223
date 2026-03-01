/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.robot;

/**
 * The RobotType enum defines various types of robots utilized in the colony 
 * environment. Each enum constant represents a specific class of robot, 
 * designed for distinct tasks and functionalities. This enumeration 
 * facilitates the management of different robot roles within the colony,
 * allowing for more organized and efficient task allocation and execution.
 * <p>
 * Examples of robot types include:
 * <ul>
 *   <li><b>Cartographer</b> for mapping and exploration,</li>
 *   <li><b>Harvester</b> for agricultural tasks,</li>
 *   <li><b>Miner</b> for resource extraction,</li>
 *   <li><b>Pipeliner</b> for constructing pipelines, and</li>
 *   <li><b>Farmer</b> for managing crop cultivation.</li>
 * </ul>
 * <p>
 * Note: The {@code Centralizer} is not a robot and therefore does not appear here.
 * It is a base-resident coordinator with no sensors or effectors; see
 * {@link fr.ensicaen.lv223.colony.robot.Centralizer}.
 * </p>
 */
public enum RobotType {
    CARTOGRAPHER,
    HARVESTER,
    MINER,
    PIPELINER,
    FARMER,
    UNKNOWN;

    /**
     * Returns a human-readable formatted string for this robot type.
     *
     * @return a formatted string (e.g., "Cartographer" for CARTOGRAPHER)
     */
    public String toFormattedString() {
        String lowerCase = name().toLowerCase();
        return lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
    }

    public static RobotType fromString(String s) {
        return RobotType.valueOf(s.trim().toUpperCase());
    }

}

