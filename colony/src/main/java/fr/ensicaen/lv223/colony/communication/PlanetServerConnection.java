/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages communication between the colony and the planet server in the
 * LV-223 simulation.
 * <p>
 * This class encapsulates the logic for establishing a socket connection,
 * sending requests, receiving responses, and handling connection errors.
 * It also provides methods to coordinate turn-based synchronization with the server.
 * </p>
 */
public class PlanetServerConnection {
    private static final Logger logger = LogManager.getLogger(PlanetServerConnection.class);

    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static final long TURN_COMPLETION_TIMEOUT_MS = 5000;
    private static final long TURN_WAIT_INTERVAL_MS = 1000;
    private static final String CLIENT_ID = "COLONY_CLIENT";
    private static final String QUIT_COMMAND = "quit";
    private static final String END_OF_TURN_ACTION = "endOfTurn";

    private Socket socket;
    private BufferedReader reader;
    private OutputStream writer;
    private volatile boolean isConnected;
    private volatile boolean isShutdownRequested;
    private volatile boolean turnCompleted;

    /**
     * Constructs a new PlanetServerConnection and establishes a connection.
     *
     * @param serverAddress the server address
     * @param port          the server port
     */
    public PlanetServerConnection(String serverAddress, int port) {
        try {
            connect(serverAddress, port);
            isConnected = true;
        } catch (IOException e) {
            logger.error("Error establishing server connection: {}", e.getMessage());
            isConnected = false;
        }
        isShutdownRequested = false;
    }

    /**
     * Establishes a connection with the planet server.
     *
     * @param serverAddress the server address
     * @param port          the server port
     * @throws IOException if an I/O error occurs during connection
     */
    private void connect(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = socket.getOutputStream();
        // Identify as a colony client upon connection.
        send(CLIENT_ID + "\n");
    }

    /**
     * Closes the connection to the server.
     */
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            logger.error("Error closing reader: {}", e.getMessage());
        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            logger.error("Error closing writer: {}", e.getMessage());
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error("Error closing socket: {}", e.getMessage());
        } finally {
            isConnected = false;
        }
    }

    /**
     * Sends a request to the server and waits for a response.
     *
     * @param request the request string to send
     * @return the server response, or null if an error occurs
     */
    public String sendRequest(String request) {
        if (!isConnected) {
            logger.error("Cannot send request: not connected to server.");
            return null;
        }
        try {
            send(request + "\n");
            return receiveResponse();
        } catch (IOException e) {
            handleConnectionError(e);
            return null; // Unreachable because handleConnectionError() throws an exception.
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message the message to send
     * @throws IOException if an I/O error occurs
     */
    private void send(String message) throws IOException {
        writer.write(message.getBytes(StandardCharsets.UTF_8));
        writer.flush();
    }

    /**
     * Receives a response from the server.
     *
     * @return the response string
     */
    private String receiveResponse() {
        try {
            String response = reader.readLine();
            if (response == null) {
                throw new IOException("Server closed the connection.");
            }
            // If a quit command is received, mark shutdown and propagate a ConnectionClosedException.
            if (response.trim().equalsIgnoreCase(QUIT_COMMAND)) {
                isShutdownRequested = true;
                throw new ConnectionClosedException("Received quit command from server.", null);
            }
            logger.debug("Server answered: {}", response);
            return response;
        } catch (IOException e) {
            handleConnectionError(e);
            return null; // Unreachable because handleConnectionError() throws an exception.
        }
    }

    /**
     * Handles errors during communication with the server.
     *
     * @param e the exception that occurred
     * @throws ConnectionClosedException to indicate that the connection has been lost
     */
    private void handleConnectionError(Exception e) {
        logger.error("Error communicating with the server: {}", e.getMessage());
        this.isConnected = false;
        // Notify the caller by throwing an unchecked exception.
        throw new ConnectionClosedException("Connection to the server has been lost.", e);
    }

    /**
     * Checks if the connection to the server is still active.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Checks if the server has requested to shut down.
     *
     * @return true if the server has requested shutdown, false otherwise
     */
    public boolean isShutdownRequested() {
        return isShutdownRequested;
    }

    /**
     * Sends an end-of-turn message to the server.
     */
    public void sendEndOfTurnMessage() {
        String request = "{ \"action\": \"" + END_OF_TURN_ACTION + "\" }";
        String response = sendRequest(request);
        if (response != null && response.contains("success") && response.contains(END_OF_TURN_ACTION)) {
            synchronized (this) {
                turnCompleted = true;
                notifyAll();
            }
        } else if (!isConnected) {
            logger.error("Cannot send end-of-turn message: not connected to server.");
        }
    }

    /**
     * Waits until the server indicates that the turn is complete.
     */
    public synchronized void waitForTurnCompletion() {
        long startTime = System.currentTimeMillis();
        try {
            while (!turnCompleted && isConnected()) {
                wait(TURN_WAIT_INTERVAL_MS);
                if (System.currentTimeMillis() - startTime > TURN_COMPLETION_TIMEOUT_MS) {
                    logger.warn("Turn completion timeout reached.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while waiting for turn completion", e);
        }
        logger.debug("Wait for turn completion finished");
    }

    /**
     * Resets the turn completion flag.
     */
    public synchronized void resetTurnCompletion() {
        turnCompleted = false;
        logger.debug("Turn completion flag has been reset");
    }
}
