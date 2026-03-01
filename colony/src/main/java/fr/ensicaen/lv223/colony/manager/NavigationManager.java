/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.manager;

import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.utils.Direction;

/**
 * Manages all movement‐related operations for a robot.
 * <p>
 * This class delegates movement requests to the {@code RobotEnvironmentFacade},
 * ensuring that the robot's movement is coordinated through the centralized communication layer.
 * </p>
 */
public class NavigationManager {

    /** The robot associated with this manager. */
    private final Robot robot;

    /** The facade used to interact with the environment. */
    private final RobotEnvironmentFacade environmentFacade;

    /**
     * Constructs a new NavigationManager.
     *
     * @param robot the robot to manage
     * @param environmentFacade the environment facade used for sending move requests
     */
    public NavigationManager(Robot robot, RobotEnvironmentFacade environmentFacade) {
        this.robot = robot;
        this.environmentFacade = environmentFacade;
    }

    /**
     * Moves the robot in the specified direction.
     *
     * @param direction the direction in which to move the robot
     */
    public void move(Direction direction) {
        // Delegate the move operation to the environment facade.
        environmentFacade.moveRobot(robot, direction);
    }
}
