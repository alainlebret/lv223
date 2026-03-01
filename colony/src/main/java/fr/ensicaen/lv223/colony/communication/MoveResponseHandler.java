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
 * Handles the response from the server after a move action request.
 * <p>
 * This response handler processes the outcome of a move operation. It converts
 * the target global coordinate returned by the server into the robot's local
 * coordinate system using the base coordinates and updates the robot's current
 * location accordingly.
 * </p>
 */
public class MoveResponseHandler extends AbstractResponseHandler {

    /**
     * Global X coordinate of the colony base.
     */
    private final int baseX;
    
    /**
     * Global Y coordinate of the colony base.
     */
    private final int baseY;
   
    /**
     * Constructs a MoveResponseHandler with the provided base coordinates.
     *
     * @param baseX global X coordinate of the colony base
     * @param baseY global Y coordinate of the colony base
     */
    public MoveResponseHandler(int baseX, int baseY) {
        this.baseX = baseX;
        this.baseY = baseY;
    }
   
    /**
     * Updates the robot's location based on the target global coordinate.
     * <p>
     * This method converts the provided global coordinate to a local coordinate
     * relative to the colony base using the static helper method
     * {@code RobotEnvironmentFacade.translateToLocalStatic} and then updates the
     * robot's current location.
     * </p>
     *
     * @param response     the action response from the server (not used in this implementation)
     * @param robot        the robot that initiated the move action
     * @param targetGlobal the target global coordinate as returned by the server
     */
    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        Coordinate newLocal = RobotEnvironmentFacade.translateToLocalStatic(targetGlobal, baseX, baseY);
        robot.setCurrentLocation(newLocal);
    }
}
