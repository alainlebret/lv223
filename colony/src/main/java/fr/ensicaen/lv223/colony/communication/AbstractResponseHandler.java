/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.utils.Coordinate;

/**
 * An abstract base class that centralizes common response-handling logic.
 * <p>
 * This class implements the {@code ResponseHandler} interface and performs
 * shared tasks such as logging and status checking. Subclasses need only
 * implement the {@link #updateRobotState(ActionResponse, Robot, Coordinate)}
 * method to update the robot state based on the specific action response.
 * </p>
 */
public abstract class AbstractResponseHandler implements ResponseHandler {
    protected final Logger logger = LogManager.getLogger(getClass());

    @Override
    public void handleResponse(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        String robotName = (robot != null) ? robot.getName() : "unknown";
        if (response == null) {
            logger.error("Null response received for robot {}", robotName);
            return;
        }
        if (robot == null) {
            logger.error("Null robot provided for action '{}'", response.getAction());
            return;
        }
        if (!"success".equalsIgnoreCase(response.getStatus())) {
            logger.error("Action '{}' failed for robot {}: {}",
                response.getAction(), robotName, response.getMessage());
            return;
        }
        logger.info("Action '{}' successful for robot {}", response.getAction(), robotName);
        updateRobotState(response, robot, targetGlobal);
    }

    /**
     * Updates the robot's state based on the specific action response.
     * <p>
     * Subclasses must implement this method to handle updates for actions such
     * as move, harvest, scan, etc.
     * </p>
     *
     * @param response     the parsed action response
     * @param robot        the robot that initiated the action
     * @param targetGlobal the target global coordinate (if applicable)
     */
    protected abstract void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal);
}

