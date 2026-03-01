/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.exception;

/**
 * Exception thrown when an invalid quantity value is provided.
 * <p>
 * This exception is used to indicate that a method received a quantity value
 * that is either less than 0 or exceeds the maximum allowed for the given resource
 * type. The exception message should provide details about the invalid value.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class InvalidQuantityException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InvalidQuantityException with the specified detail message.
     *
     * @param message a detailed message explaining the invalid quantity value
     */
    public InvalidQuantityException(String message) {
        super(message);
    }
}
