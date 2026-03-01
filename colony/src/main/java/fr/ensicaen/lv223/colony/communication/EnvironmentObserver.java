/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

/**
 * The EnvironmentObserver interface defines the protocol for objects that need 
 * to be notified about changes or events occurring in the simulation environment.
 * <p>
 * This interface is part of the Observer design pattern and is typically implemented 
 * by robot entities or other agents that require real-time updates from the environment.
 * Implementers subscribe to an EnvironmentFacade (the subject), and their 
 * {@code update} method is called whenever a relevant event or change occurs.
 * </p>
 *
 * @see EnvironmentFeedback
 */
public interface EnvironmentObserver {
    /**
     * Called when the environment has new feedback to report.
     * Implementers should update their internal state based on the provided feedback.
     *
     * @param feedback the feedback describing the environment's current state or event.
     */
    void update(EnvironmentFeedback feedback);
}
