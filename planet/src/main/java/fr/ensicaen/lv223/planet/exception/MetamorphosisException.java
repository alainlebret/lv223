/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.exception;

/**
 * Exception thrown when an error occurs during planet metamorphosis.
 * <p>
 * This exception is raised when issues disrupt the normal sequence of 
 * metamorphosis events or transformations on the planet.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class MetamorphosisException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new MetamorphosisException with the specified detail message.
     *
     * @param message the detail message explaining the metamorphosis error
     */
    public MetamorphosisException(String message) {
        super(message);
    }
}
