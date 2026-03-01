/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.strategy;

import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.manager.BatteryManager;
import fr.ensicaen.lv223.colony.utils.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DefaultRobotStrategy defines the default operational behavior for a robot.
 * <p>
 * This strategy implements a simple behavior: the robot selects a random direction,
 * moves in that direction, performs an environmental scan, and then updates its battery
 * consumption. The battery consumption is calculated using a constant alpha value.
 * </p>
 */
public class DefaultRobotStrategy implements RobotStrategy {
    private static final Logger logger = LogManager.getLogger(DefaultRobotStrategy.class);
    
    /** Alpha value for battery consumption when moving */
    private static final double MOVE_ALPHA = 1.0;
    
    private final Robot robot;
    private final RobotEnvironmentFacade facade;
    
    /**
     * Constructs a DefaultRobotStrategy with the given robot and environment facade.
     *
     * @param robot  the robot that will execute this strategy
     * @param facade the environment facade for server communication
     */
    public DefaultRobotStrategy(Robot robot, RobotEnvironmentFacade facade) {
        this.robot = robot;
        this.facade = facade;
    }
    
    /**
     * Executes the default strategy.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Retrieves the current battery level from the robot's health data.</li>
     *   <li>Selects a random direction from the available directions.</li>
     *   <li>Issues move and scan commands through the environment facade.</li>
     *   <li>Updates the robot's battery level using the {@code BatteryManager} to reflect
     *       consumption over one turn.</li>
     * </ol>
     * </p>
     */
    @Override
    public void execute() {
        // Retrieve current battery level.
        double currentBattery = robot.getHealthData().getEnergyLevel();
        
        // Select a random movement direction.
        List<Direction> directions = new ArrayList<>(Direction.MOVABLE);
        Collections.shuffle(directions);
        Direction chosen = directions.get(0);
        logger.info("Robot {} moves in direction {}", robot.getName(), chosen);
        
        // Execute movement and scanning.
        facade.moveRobot(robot, chosen);
        facade.scan(robot);

        // Calculate new battery level after movement (1 turn = 1 day)
        int c = (int) Math.round(robot.getHealthData().getEnergyLevel()); // battery in %, 0..100
        double E = BatteryManager.toEnergyUnits(c);       // energy units, 0..40
        E = BatteryManager.consume(E, MOVE_ALPHA);        // consume alpha units for a move
        int cNext = BatteryManager.toPercent(E);          // back to %, 0..100
        robot.getHealthData().setEnergyLevel(cNext);

        logger.info(
                "Battery updated from {}% to {}% after movement.",
                c, cNext
        );
    }
}
