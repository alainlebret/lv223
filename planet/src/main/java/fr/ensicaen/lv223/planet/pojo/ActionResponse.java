/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.pojo;

import java.util.List;

/**
 * Plain Old Java Object (POJO) representing a response to an action request from clients.
 * It encapsulates the status, action, message, and any additional details such as affected robots
 * or detected cells.
 */
public class ActionResponse {

    private String status;
    private String action;
    private String message;
    private List<AffectedRobot> affectedRobots;
    private List<DetectedCell> detectedCells;

    /**
     * Default constructor initializes all fields to default empty values.
     */
    public ActionResponse() {
        this.status = "";
        this.action = "";
        this.message = "";
        this.affectedRobots = null;
        this.detectedCells = null;
    }

    /**
     * Returns the status of the response.
     *
     * @return the response status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the response.
     *
     * @param status the response status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the action that was performed.
     *
     * @return the action name.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action that was performed.
     *
     * @param action the action name to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the descriptive message associated with the response.
     *
     * @return the response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the descriptive message for the response.
     *
     * @param message the response message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the list of robots affected by the action.
     *
     * @return a list of affected robots, or null if none.
     */
    public List<AffectedRobot> getAffectedRobots() {
        return affectedRobots;
    }

    /**
     * Sets the list of robots affected by the action.
     *
     * @param affectedRobots a list of affected robots.
     */
    public void setAffectedRobots(List<AffectedRobot> affectedRobots) {
        this.affectedRobots = affectedRobots;
    }

    /**
     * Returns the list of cells detected as a result of the action.
     *
     * @return a list of detected cells, or null if none.
     */
    public List<DetectedCell> getDetectedCells() {
        return detectedCells;
    }

    /**
     * Sets the list of cells detected as a result of the action.
     *
     * @param detectedCells a list of detected cells.
     */
    public void setDetectedCells(List<DetectedCell> detectedCells) {
        this.detectedCells = detectedCells;
    }
}
