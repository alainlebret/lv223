/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.strategy.HarvesterRobotStrategy;

/**
 * Represents a harvester robot in the colony simulation.
 * <p>
 * A harvester robot is specialized in gathering fruits and vegetables from the environment.
 * This class initializes the robot with a harvester-specific strategy, which encapsulates the
 * decision-making process: determining whether to harvest resources when on a suitable cell,
 * or to move and scan the environment otherwise.
 * </p>
 */
public class Harvester extends Robot {
    private static final Logger logger = LogManager.getLogger(Harvester.class);
    
    /**
     * Constructs a new Harvester robot.
     * <p>
     * The robot is initialized with the "Harvester" type and subscribes to the provided
     * environment facade. It then sets its default strategy to {@link HarvesterRobotStrategy},
     * which governs its harvesting behavior.
     * </p>
     *
     * @param facade the {@code RobotEnvironmentFacade} used for server communication and environment updates.
     */
    public Harvester(RobotEnvironmentFacade facade) {
        super(RobotType.HARVESTER, facade);
        logger.info("Setting harvester strategy for robot {}", getName());
        this.setStrategy(new HarvesterRobotStrategy(this, facade));
    }
}
