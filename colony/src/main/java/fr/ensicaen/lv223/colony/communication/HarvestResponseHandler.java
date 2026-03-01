/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.utils.Coordinate;

/**
 * Handles the response from the server after a harvest action request.
 * <p>
 * This handler is responsible for processing the outcome of a harvest action.
 * Typically, it would update the robot’s inventory or health status based on
 * the response; however, in this basic implementation, it simply logs the success.
 * </p>
 */
public class HarvestResponseHandler extends AbstractResponseHandler {

    /**
     * Updates the robot's state after a successful harvest action.
     * <p>
     * In this basic implementation, no state is modified beyond logging. 
     * Future implementations could, for example, adjust the robot's inventory.
     * </p>
     *
     * @param response     the parsed action response from the server.
     * @param robot        the robot that initiated the harvest action.
     * @param targetGlobal the target global coordinate for the action (if applicable).
     */
    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        logger.info("Harvest successful for robot {}", robot.getName());
        // Future implementation: update the robot's inventory or adjust health status.
    }
}
