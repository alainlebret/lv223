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

/**
 * Represents a miner robot in the colony simulation.
 * <p>
 * The miner is responsible for extracting mineral resources from the environment.
 * It communicates with the planet server through the {@code RobotEnvironmentFacade}.
 * Currently, it uses the {@code DefaultRobotStrategy} as its operational strategy,
 * which may later be replaced with a dedicated miner strategy.
 * </p>
 */
public class Miner extends Robot {
    private static final Logger logger = LogManager.getLogger(Miner.class);

    /**
     * Constructs a new Miner robot with access to the environment facade.
     * <p>
     * The robot is initialized with a default strategy. In the future, this
     * default strategy can be replaced with a dedicated miner strategy if desired.
     * </p>
     *
     * @param facade the {@code RobotEnvironmentFacade} that provides the robot with
     *               environmental updates and communication capabilities with the server.
     */
    public Miner(RobotEnvironmentFacade facade) {
        super(RobotType.MINER, facade);
        logger.info("Initializing Miner with default strategy.");
        // TODO: Call setStrategy(new MinerRobotStrategy(this, facade)) once implemented.
    }
}
