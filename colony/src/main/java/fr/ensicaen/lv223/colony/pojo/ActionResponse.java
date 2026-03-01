/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.pojo;

import java.util.Collections;
import java.util.List;

/**
 * A Plain Old Java Object (POJO) representing a response to an action request from clients.
 * <p>
 * This class encapsulates the details of a server response, including the status, action type,
 * message, and any additional data such as affected robots or detected cells.
 * </p>
 */
public class ActionResponse {
    private String status;
    private String action;
    private String message;
    private List<AffectedRobot> affectedRobots;
    private List<DetectedCell> detectedCells;

    /**
     * Default constructor that initializes fields with safe default values.
     */
    public ActionResponse() {
        this.status = "";
        this.action = "";
        this.message = "";
        this.affectedRobots = Collections.emptyList();
        this.detectedCells = Collections.emptyList();
    }

    /**
     * Returns the status of the response.
     *
     * @return the response status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the response.
     *
     * @param status the response status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the action associated with the response.
     *
     * @return the action name
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action associated with the response.
     *
     * @param action the action name to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the message included in the response.
     *
     * @return the response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message included in the response.
     *
     * @param message the response message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the list of affected robots in the response.
     *
     * @return the list of affected robots
     */
    public List<AffectedRobot> getAffectedRobots() {
        return affectedRobots;
    }

    /**
     * Sets the list of affected robots in the response.
     *
     * @param affectedRobots the list of affected robots to set
     */
    public void setAffectedRobots(List<AffectedRobot> affectedRobots) {
        this.affectedRobots = affectedRobots;
    }

    /**
     * Returns the list of detected cells included in the response.
     *
     * @return the list of detected cells
     */
    public List<DetectedCell> getDetectedCells() {
        return detectedCells;
    }

    /**
     * Sets the list of detected cells included in the response.
     *
     * @param detectedCells the list of detected cells to set
     */
    public void setDetectedCells(List<DetectedCell> detectedCells) {
        this.detectedCells = detectedCells;
    }
}
