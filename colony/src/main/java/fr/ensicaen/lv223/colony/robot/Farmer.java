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
import fr.ensicaen.lv223.colony.strategy.FarmerRobotStrategy;

/**
 * Represents a farmer robot in the colony simulation.
 * <p>
 * A farmer robot is specialized in agricultural tasks, such as cultivating crops
 * and maintaining productive land. This class initializes the robot with the "Farmer" type
 * and sets a farmer-specific strategy that dictates its behavior in the environment.
 * </p>
 */
public class Farmer extends Robot {
    private static final Logger logger = LogManager.getLogger(Farmer.class);

    /**
     * Constructs a new Farmer robot.
     * <p>
     * The robot is initialized with the "Farmer" type and is subscribed to the provided
     * {@code RobotEnvironmentFacade} for environment communication. Its default strategy is
     * set to {@link FarmerRobotStrategy}, which governs its farming behavior.
     * </p>
     *
     * @param facade the {@code RobotEnvironmentFacade} that supplies environmental data and
     *               communication capabilities with the server.
     */
    public Farmer(RobotEnvironmentFacade facade) {
        super(RobotType.FARMER, facade);
        logger.info("Initializing Farmer robot with strategy for {}", getName());
        this.setStrategy(new FarmerRobotStrategy(this, facade));
    }
}
