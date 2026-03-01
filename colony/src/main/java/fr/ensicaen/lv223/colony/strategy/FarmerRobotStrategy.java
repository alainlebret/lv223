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
import fr.ensicaen.lv223.colony.utils.CellType;
import fr.ensicaen.lv223.colony.utils.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Strategy for farmer robots.
 * <p>
 * A farmer robot should:
 * <ol>
 *   <li>Wait until the base is supplied with water (via pipelines) before starting.</li>
 *   <li>Locate arable cells ({@code PRAIRIE}, {@code WET_PRAIRIE}, or {@code DRY_PRAIRIE}).</li>
 *   <li>Cultivate the cell by calling {@code facade.cultivate(robot, waterUnits)} each turn
 *       for the required number of turns (15/30/60 depending on prairie type).</li>
 *   <li>Once cultivation is complete, move on to find another arable cell.</li>
 *   <li>Manage battery level and return to base for recharging when needed.</li>
 * </ol>
 * Refer to the subject for water consumption rates per prairie type.
 * </p>
 *
 * @see DefaultRobotStrategy for a minimal example of how to interact with the facade
 * @see BatteryManager for battery consumption calculations
 */
public class FarmerRobotStrategy implements RobotStrategy {
    private static final Logger logger = LogManager.getLogger(FarmerRobotStrategy.class);

    // Battery consumption factors (alpha values from the subject).
    private static final double MOVE_ALPHA = 1.1;
    private static final double CULTIVATE_ALPHA = 2.0;
    private static final double TURN_DURATION = 1.0;

    private final Robot robot;
    private final RobotEnvironmentFacade facade;

    /**
     * Constructs a new FarmerRobotStrategy.
     *
     * @param robot  the farmer robot that will execute this strategy
     * @param facade the environment facade used for communication with the server
     */
    public FarmerRobotStrategy(Robot robot, RobotEnvironmentFacade facade) {
        this.robot = robot;
        this.facade = facade;
    }

    /**
     * Executes the farmer strategy for one turn.
     * <p>
     * TODO: Implement the farmer behavior. Suggested steps:
     * <ol>
     *   <li>Check if the base has water available (pipeline connected).</li>
     *   <li>If on an arable cell, call {@code facade.cultivate(robot, waterUnits)}
     *       with the appropriate water amount for the prairie type.</li>
     *   <li>If not on an arable cell, move toward one (or explore to find one).</li>
     *   <li>Update the battery level using {@code BatteryManager.updateBatteryLevel()}.</li>
     * </ol>
     * </p>
     */
    @Override
    public void execute() {
        // TODO: Implement farmer strategy.
    }
}
