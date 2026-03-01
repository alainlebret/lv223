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

import java.util.Map;
import java.util.Random;

/**
 * Strategy for harvester robots.
 * <p>
 * A harvester robot should:
 * <ol>
 *   <li>Explore the planet to find {@code FRUITS_AND_VEGETABLES} cells.</li>
 *   <li>Harvest resources when on a suitable cell (max 100 units/turn).</li>
 *   <li>Return harvested resources to the base.</li>
 *   <li>Manage its battery level and return to base for recharging when needed.</li>
 * </ol>
 * </p>
 *
 * @see DefaultRobotStrategy for a minimal example of how to interact with the facade
 * @see BatteryManager for battery consumption calculations
 */
public class HarvesterRobotStrategy implements RobotStrategy {
    private static final Logger logger = LogManager.getLogger(HarvesterRobotStrategy.class);

    // Battery consumption factors (alpha values from the subject).
    private static final double MOVE_ALPHA = 1.1;
    private static final double HARVEST_ALPHA = 1.5;
    private static final double TURN_DURATION = 1.0;

    private final Robot robot;
    private final RobotEnvironmentFacade facade;
    private final Random random = new Random();

    /**
     * Constructs a new HarvesterRobotStrategy.
     *
     * @param robot  the robot that will execute this strategy.
     * @param facade the environment facade for server communication.
     */
    public HarvesterRobotStrategy(Robot robot, RobotEnvironmentFacade facade) {
        this.robot = robot;
        this.facade = facade;
    }

    /**
     * Executes the harvester strategy for one turn.
     * <p>
     * TODO: Implement the harvester behavior. Suggested steps:
     * <ol>
     *   <li>Scan the environment ({@code facade.scan(robot)}).</li>
     *   <li>Decide what to do: move toward a food cell, harvest, or return to base.</li>
     *   <li>Execute the chosen action using the facade (e.g., {@code facade.harvest(robot, units)},
     *       {@code facade.moveRobot(robot, direction)}).</li>
     *   <li>Update the battery level using {@code BatteryManager.updateBatteryLevel()}.</li>
     * </ol>
     * </p>
     */
    @Override
    public void execute() {
        // TODO: Implement harvester strategy.
    }
}
