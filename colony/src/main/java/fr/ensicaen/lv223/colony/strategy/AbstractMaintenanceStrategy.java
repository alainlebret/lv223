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
import fr.ensicaen.lv223.colony.robot.RobotType;
import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.utils.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Abstract base strategy for maintenance actions (e.g., battery recharge and repair).
 * <p>
 * This class implements the common behavior required during maintenance:
 * <ul>
 *   <li>If the robot is not at the base, it will move toward the base.</li>
 *   <li>If the robot is at the base, it waits a fixed number of turns, then restores its state and exits the base.</li>
 * </ul>
 * Concrete subclasses may override {@link #restoreRobotState()} if additional restoration is needed.
 * </p>
 */
public abstract class AbstractMaintenanceStrategy implements RobotStrategy {
    protected static final Logger logger = LogManager.getLogger(AbstractMaintenanceStrategy.class);
    
    /** Number of turns to wait at base during maintenance */
    protected static final int WAIT_TURNS_AT_BASE = 3;
    
    protected final Robot robot;
    protected final RobotEnvironmentFacade facade;
    
    /** Counter for remaining wait turns at the base */
    protected int remainingWaitTurns;
    protected final Random random = new Random();

    /**
     * Constructs a maintenance strategy for the specified robot.
     *
     * @param robot  the robot that will perform maintenance
     * @param facade the environment facade for communication
     */
    public AbstractMaintenanceStrategy(Robot robot, RobotEnvironmentFacade facade) {
        this.robot = robot;
        this.facade = facade;
        this.remainingWaitTurns = WAIT_TURNS_AT_BASE;
    }

    /**
     * Executes the maintenance strategy.
     * <p>
     * Behavior:
     * <ol>
     *   <li>If the robot is at the base, it will wait for the configured number of turns.
     *       During this period, it may perform scans to update its internal state.</li>
     *   <li>Once the wait period is over, the robot’s state is restored (e.g., battery recharged),
     *       the wait counter is reset, and the robot is forced to leave the base.</li>
     *   <li>If the robot is not at the base, it will continue moving toward the base.</li>
     * </ol>
     * </p>
     */
    @Override
    public void execute() {
        Coordinate currentLoc = robot.getCurrentLocation();
        if (isAtBase(currentLoc)) {
            if (remainingWaitTurns > 0) {
                logger.info("Robot {} is undergoing maintenance at base. Waiting {} more turns.", 
                        robot.getName(), remainingWaitTurns);
                remainingWaitTurns--;
                // Optionally perform a scan while waiting.
                facade.scan(robot);
                return;
            } else {
                // Maintenance is complete: restore the robot's state.
                restoreRobotState();
                logger.info("Robot {} maintenance complete. Exiting base.", robot.getName());
                // Reset the wait counter for future maintenance cycles.
                remainingWaitTurns = WAIT_TURNS_AT_BASE;
                // Force the robot to leave the base.
                Direction exitDir = chooseLeaveBaseDirection();
                facade.moveRobot(robot, exitDir);
                // Switch back to the default (operational) strategy.
                switchDefaultStrategy();
                return;
            }
        } else {
            // The robot is not at the base: continue moving toward the base.
            Direction moveDir = calculateDirectionToBase(currentLoc);
            logger.info("Robot {} returning to base by moving {}.", robot.getName(), moveDir);
            facade.moveRobot(robot, moveDir);
        }
    }

    /**
     * Checks whether the specified local coordinate represents the base.
     * <p>
     * In our local coordinate system, the base is assumed to be at (0,0).
     * </p>
     *
     * @param local the robot's local coordinate
     * @return {@code true} if the coordinate is (0,0); {@code false} otherwise
     */
    protected boolean isAtBase(Coordinate local) {
        return local.getX() == 0 && local.getY() == 0;
    }

    /**
     * Computes the direction to move from the current coordinate toward the base.
     * <p>
     * The base is assumed to be located at (0,0) in the local coordinate system.
     * </p>
     *
     * @param current the current local coordinate of the robot
     * @return the {@code Direction} that brings the robot closer to (0,0)
     */
    protected Direction calculateDirectionToBase(Coordinate current) {
        int dx = -current.getX();
        int dy = -current.getY();

        if (dx == 0 && dy == 0) {
            return Direction.NONE;
        }
        // Determine the proper cardinal or diagonal direction.
        if (dx > 0 && dy > 0) {
            return Direction.SOUTHEAST;
        } else if (dx > 0 && dy < 0) {
            return Direction.NORTHEAST;
        } else if (dx < 0 && dy > 0) {
            return Direction.SOUTHWEST;
        } else if (dx < 0 && dy < 0) {
            return Direction.NORTHWEST;
        } else if (dx > 0) {
            return Direction.EAST;
        } else if (dx < 0) {
            return Direction.WEST;
        } else if (dy > 0) {
            return Direction.SOUTH;
        } else { // dy < 0
            return Direction.NORTH;
        }
    }

    /**
     * Chooses a random cardinal direction for the robot to leave the base.
     *
     * @return a random {@code Direction} among NORTH, SOUTH, EAST, or WEST
     */
    protected Direction chooseLeaveBaseDirection() {
        Direction[] cardinals = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
        return cardinals[random.nextInt(cardinals.length)];
    }

    /**
     * Switches the robot's strategy back to its default operational strategy.
     * <p>
     * The default strategy is selected based on the robot's type.
     * </p>
     * TODO: Can be enhanced if you define new default operational strategy. 
     */
    protected void switchDefaultStrategy() {
        switch (robot.getType()) {
            case FARMER:
                robot.setStrategy(new FarmerRobotStrategy(robot, facade));
                break;
            case HARVESTER:
                robot.setStrategy(new HarvesterRobotStrategy(robot, facade));
                break;
            case CARTOGRAPHER:
            case MINER:
            case PIPELINER:
            default:
                robot.setStrategy(new DefaultRobotStrategy(robot, facade));
                break;
        }
    }
    
    /**
     * Restores the robot's state at the end of maintenance.
     * <p>
     * By default, this method sets the robot's energy level to 100.
     * Subclasses may override this method to apply additional restoration logic.
     * </p>
     */
    protected void restoreRobotState() {
        robot.getHealthData().setEnergyLevel(100);
        logger.info("Robot {} state restored (battery set to 100%).", robot.getName());
    }
}
