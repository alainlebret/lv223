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
 * Defines a contract for handling responses from the server after an action request.
 * <p>
 * Implementers of this interface are responsible for processing the parsed
 * {@link ActionResponse} received from the server and updating the state of the
 * corresponding {@link Robot} accordingly. The target global coordinate may be used
 * to translate server responses into the robot's local coordinate system.
 * </p>
 */
public interface ResponseHandler {

    /**
     * Processes the server's response for an action request.
     *
     * @param response     the parsed {@link ActionResponse} from the server.
     * @param robot        the {@link Robot} that initiated the action.
     * @param targetGlobal the target global coordinate associated with the action,
     *                     if applicable.
     */
    void handleResponse(ActionResponse response, Robot robot, Coordinate targetGlobal);
}
