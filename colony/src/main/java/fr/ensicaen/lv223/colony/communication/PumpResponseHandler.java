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
 * Handles the response from the server after a pump action request.
 * <p>
 * This handler is responsible for processing the outcome of a pump action,
 * which extracts water from a water-bearing cell via an operational pipeline.
 * Typically, it would update the robot's carried water count or the cell's
 * known water level in the local map; in this basic implementation it simply
 * logs the success.
 * </p>
 */
public class PumpResponseHandler extends AbstractResponseHandler {

    /**
     * Updates the robot's state after a successful pump action.
     * <p>
     * In this basic implementation, no state is modified beyond logging.
     * Future implementations could, for example, record the pumped water
     * quantity in the robot's local map or inventory.
     * </p>
     *
     * @param response     the parsed action response from the server.
     * @param robot        the robot that initiated the pump action.
     * @param targetGlobal the global coordinate of the pumped cell.
     */
    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        logger.info("Pump successful for robot {}", robot.getName());
        // Future implementation: record pumped water quantity or update local map.
    }
}
