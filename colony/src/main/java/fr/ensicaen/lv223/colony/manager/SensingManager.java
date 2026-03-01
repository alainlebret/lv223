/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.manager;

import fr.ensicaen.lv223.colony.communication.EnvironmentFeedback;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;

/**
 * Manages environment scanning and updates the robot's local perception.
 * <p>
 * This class delegates the scan operation to the {@code RobotEnvironmentFacade}
 * and can be extended to process scan feedback to update the robot’s internal state.
 * </p>
 */
public class SensingManager {

    /** The robot associated with this sensing manager. */
    private final Robot robot;

    /** The facade used to interact with the environment. */
    private final RobotEnvironmentFacade environmentFacade;

    /**
     * Constructs a new SensingManager instance.
     *
     * @param robot the robot associated with this manager
     * @param facade the environment facade used to perform scanning operations
     */
    public SensingManager(Robot robot, RobotEnvironmentFacade facade) {
        this.robot = robot;
        this.environmentFacade = facade;
    }

    /**
     * Initiates an environment scan.
     * <p>
     * This method delegates the scan request to the environment facade.
     * </p>
     */
    public void scan() {
        environmentFacade.scan(robot);
    }

    /**
     * Called by {@link fr.ensicaen.lv223.colony.robot.Robot#update} whenever a
     * {@code SCAN_RESULT} feedback arrives.
     * <p>
     * Note: the core map update (populating the robot’s {@code LocalMap}) is already
     * performed synchronously by {@code ScanResponseHandler} before this method is
     * invoked. Override or extend this method to add secondary processing — for example,
     * updating a learning model or triggering a re-planning step.
     * </p>
     *
     * @param feedback the scan feedback received from the server
     */
    public void update(EnvironmentFeedback feedback) {
        // Extension point: add post-scan processing here if needed.
    }
}
