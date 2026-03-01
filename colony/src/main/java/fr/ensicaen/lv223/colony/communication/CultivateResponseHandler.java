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
 * Handles the response from the server for a cultivate action request.
 * <p>
 * This response handler processes the cultivate action by updating the robot's state.
 * Currently, it logs the successful cultivate action. In a more complete implementation,
 * this class could also update the robot's inventory, status, or any other relevant state.
 * </p>
 */
public class CultivateResponseHandler extends AbstractResponseHandler {

    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        // Log the successful cultivate action.
        logger.info("Cultivate action successful for robot {}", robot.getName());
        
        // Future improvements:
        // - Update the robot's inventory or crop yield.
        // - Adjust the robot's status if necessary.
        // - Use the targetGlobal coordinate if needed for further state management.
    }
}
