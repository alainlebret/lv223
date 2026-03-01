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
 * Represents a cartographer robot in the colony simulation.
 * <p>
 * A cartographer is responsible for exploring and mapping the environment.
 * It inherits the {@link fr.ensicaen.lv223.colony.strategy.DefaultRobotStrategy}
 * from {@link Robot}; override with a dedicated cartography strategy as needed.
 * </p>
 */
public class Cartographer extends Robot {
    private static final Logger logger = LogManager.getLogger(Cartographer.class);

    /**
     * Constructs a new Cartographer with the specified environment facade.
     * <p>
     * The robot is initialized with the type "Cartographer" and a default strategy
     * provided by {@link DefaultRobotStrategy}. This strategy governs the robot's behavior
     * until it is changed by external logic.
     * </p>
     *
     * @param facade the {@code RobotEnvironmentFacade} used for communication with the server.
     */
    public Cartographer(RobotEnvironmentFacade facade) {
        super(RobotType.CARTOGRAPHER, facade);
        logger.info("Initializing Cartographer with default strategy.");
    }
}
