/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Manages robot information for the planet server.
 * <p>
 * This class maintains a concurrent map of robot identifiers to their corresponding
 * {@link RobotInfo} objects. Although not currently in active use, it provides a centralized
 * repository for storing and retrieving robot information.
 * </p>
 *
 * @since 1.0
 */
public class RobotManager {
    
    /** Map of robot IDs to their corresponding RobotInfo objects. */
    private final Map<String, RobotInfo> robots = new ConcurrentHashMap<>();

    /**
     * Adds a robot's information to the manager.
     *
     * @param id   the unique identifier of the robot
     * @param type the type of the robot
     * @param x    the x-coordinate of the robot's position
     * @param y    the y-coordinate of the robot's position
     */
    public void addRobot(String id, RobotType type, int x, int y) {
        robots.put(id, new RobotInfo(id, type, x, y));
    }

    /**
     * Retrieves the robot information associated with the given identifier.
     *
     * @param id the unique identifier of the robot
     * @return the corresponding {@link RobotInfo} object, or {@code null} if not found
     */
    public RobotInfo getRobot(String id) {
        return robots.get(id);
    }

    /**
     * Returns an unmodifiable view of all managed robot information.
     *
     * @return a Map of robot IDs to their {@link RobotInfo} objects
     */
    public Map<String, RobotInfo> getAllRobots() {
        return Collections.unmodifiableMap(robots);
    }
}
