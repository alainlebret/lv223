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
 * Handles the response from the server after a pipe action request.
 * <p>
 * This handler processes the result of a pipe construction action. In a more
 * advanced implementation, it could update the robot's local map or inventory
 * to reflect changes due to the construction of a pipeline. For now, it simply
 * logs the successful completion of the action.
 * </p>
 */
public class PipeResponseHandler extends AbstractResponseHandler {

    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        // Log the success of the pipe action.
        logger.info("Pipe action successful for robot {}", robot.getName());
    }
}

