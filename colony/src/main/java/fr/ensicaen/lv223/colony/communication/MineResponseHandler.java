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
 * Handles the response from the server after a mine action request.
 * <p>
 * This response handler processes the outcome of a mining operation. Typically, it 
 * would update the robot’s inventory or health status based on the results of the mine 
 * action. In this basic implementation, it simply logs a successful mining action.
 * </p>
 */
public class MineResponseHandler extends AbstractResponseHandler {

    /**
     * Updates the robot's state following a successful mine action.
     * <p>
     * Currently, this method logs the success of the mining operation. In future 
     * implementations, additional logic such as updating the robot’s inventory or 
     * adjusting resource counts could be added here.
     * </p>
     *
     * @param response     the parsed action response from the server.
     * @param robot        the robot that performed the mining action.
     * @param targetGlobal the target global coordinate for the mining action (if applicable).
     */
    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        logger.info("Mine successful for robot {}", robot.getName());
        // Future enhancement: update robot's inventory or resource levels based on the response.
    }
}
