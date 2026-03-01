/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.strategy;

import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.robot.Robot;

/**
 * A generic maintenance strategy that extends the common maintenance behavior defined in
 * {@link AbstractMaintenanceStrategy}. This strategy is used for basic maintenance actions,
 * such as battery recharging and minor repairs, when no specialized maintenance behavior is required.
 */
public class GenericMaintenanceStrategy extends AbstractMaintenanceStrategy {

    /**
     * Constructs a new GenericMaintenanceStrategy.
     *
     * @param robot  the robot that will perform maintenance actions.
     * @param facade the RobotEnvironmentFacade used for communication with the server.
     */
    public GenericMaintenanceStrategy(Robot robot, RobotEnvironmentFacade facade) {
        super(robot, facade);
    }
    
}
