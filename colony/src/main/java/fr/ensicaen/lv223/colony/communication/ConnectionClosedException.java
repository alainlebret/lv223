/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

/**
 * Thrown when the connection to the planet server is unexpectedly closed.
 * This runtime exception indicates that the client lost its connection
 * to the server and cannot continue communication.
 */
public class ConnectionClosedException extends RuntimeException {

    /**
     * Constructs a new ConnectionClosedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ConnectionClosedException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConnectionClosedException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public ConnectionClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ConnectionClosedException with the specified cause.
     *
     * @param cause the cause of the exception.
     */
    public ConnectionClosedException(Throwable cause) {
        super(cause);
    }
}
