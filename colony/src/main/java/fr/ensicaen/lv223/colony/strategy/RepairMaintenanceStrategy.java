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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Maintenance strategy for repairing a robot's health.
 * <p>
 * This strategy is used when the robot's health is severely degraded. It
 * inherits the common maintenance behavior from {@code AbstractMaintenanceStrategy}
 * (such as moving toward the base and waiting there) and overrides the
 * state restoration method to focus on repairing the robot's health. Specifically,
 * it resets the healthy level to 100, marks the robot as operational, and updates
 * the status message to "healthy".
 * </p>
 */
public class RepairMaintenanceStrategy extends AbstractMaintenanceStrategy {

    private static final Logger logger = LogManager.getLogger(RepairMaintenanceStrategy.class);

    /**
     * Constructs a new RepairMaintenanceStrategy for the specified robot.
     *
     * @param robot  the robot to be maintained.
     * @param facade the environment facade used for server communication.
     */
    public RepairMaintenanceStrategy(Robot robot, RobotEnvironmentFacade facade) {
        super(robot, facade);
    }
    
    /**
     * Executes the repair maintenance strategy.
     * <p>
     * This method delegates to the common maintenance behavior defined in
     * {@code AbstractMaintenanceStrategy} and applies repair-specific restoration
     * of the robot's health state.
     * </p>
     */
    @Override
    public void execute() {
        super.execute();
    }
    
    /**
     * Restores the robot's health state at the end of the maintenance period.
     * <p>
     * This implementation sets the robot's healthy level to 100, marks the robot
     * as operational, and updates the status message to "healthy", effectively
     * repairing the robot.
     * </p>
     */
    @Override
    protected void restoreRobotState() { 
        robot.getHealthData().setHealthyLevel(100);
        robot.getHealthData().setOperational(true);
        robot.getHealthData().setStatusMessage("healthy");
        logger.info("Robot {} health state restored to 100%.", robot.getName());
    }
}
