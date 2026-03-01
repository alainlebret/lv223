/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.strategy;

/**
 * Defines a strategy for a robot.
 * <p>
 * Implementations determine the behavior of a robot (e.g., movement, scanning, action execution)
 * independently of the communication layer. Each strategy is responsible for executing a
 * sequence of actions for the robot it is associated with.
 * </p>
 */
public interface RobotStrategy {
    /**
     * Executes the strategy for the associated robot.
     * <p>
     * Implementations should encapsulate the logic that determines the robot's behavior.
     * </p>
     */
    void execute();
}
