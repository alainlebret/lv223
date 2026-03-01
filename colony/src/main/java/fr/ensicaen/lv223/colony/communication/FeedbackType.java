/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

/**
 * Enumerates the types of feedback that can be communicated within the simulation environment.
 * <p>
 * This enumeration is primarily used in robot-environment interactions to specify the nature
 * of the feedback being reported. For example:
 * <ul>
 *   <li>{@code SCAN_RESULT} – Indicates that a scan operation has successfully completed and
 *       returns information about the environment.</li>
 *   <li>{@code INJURED} – Indicates that a robot has suffered damage or an injury event has occurred.</li>
 * </ul>
 * Additional feedback types may be added as the simulation evolves.
 * </p>
 */
public enum FeedbackType {
    SCAN_RESULT,
    INJURED
    // Additional feedback types can be added here.
}
