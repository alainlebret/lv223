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
 * Represents a pipeliner robot in the colony simulation.
 * <p>
 * The pipeliner is responsible for constructing and maintaining pipeline infrastructure,
 * which is crucial for resource distribution within the colony. Currently, the robot uses the
 * {@code DefaultRobotStrategy} as its default strategy. In the future, a dedicated strategy
 * (e.g. {@code PipelinerRobotStrategy}) may be implemented to handle pipelining tasks more effectively.
 * </p>
 */
public class Pipeliner extends Robot {
    private static final Logger logger = LogManager.getLogger(Pipeliner.class);

    /**
     * Constructs a new {@code Pipeliner} robot with access to the environment facade.
     * <p>
     * The robot is initialized with a default strategy, which can be replaced with a dedicated
     * pipeliner strategy if available.
     * </p>
     *
     * @param facade the {@code RobotEnvironmentFacade} that provides the robot with environmental data
     *               and communication capabilities with the server.
     */
    public Pipeliner(RobotEnvironmentFacade facade) {
        super(RobotType.PIPELINER, facade);
        logger.info("Initializing Pipeliner with default strategy.");
        // TODO: Call setStrategy(new PipelinerRobotStrategy(this, facade)) once implemented.
    }
}
