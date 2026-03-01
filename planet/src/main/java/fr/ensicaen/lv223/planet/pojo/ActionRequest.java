/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.pojo;

import java.util.Map;

/**
 * Plain Old Java Object (POJO) representing an action request from a client.
 * This class encapsulates the details of an action such as the action name,
 * the identifier and type of the requesting robot, and any additional parameters.
 */
public class ActionRequest {

    private String action;
    private String robotId;
    private String robotType;
    private Map<String, Object> parameters;

    /**
     * Returns the action name.
     *
     * @return the action name.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action name.
     *
     * @param action the action name to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the robot's identifier.
     *
     * @return the robot identifier.
     */
    public String getRobotId() {
        return robotId;
    }

    /**
     * Sets the robot's identifier.
     *
     * @param robotId the robot identifier to set.
     */
    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    /**
     * Returns the robot's type.
     *
     * @return the robot type.
     */
    public String getRobotType() {
        return robotType;
    }

    /**
     * Sets the robot's type.
     *
     * @param robotType the robot type to set.
     */
    public void setRobotType(String robotType) {
        this.robotType = robotType;
    }

    /**
     * Returns the parameters associated with the action.
     *
     * @return a map of parameter names to their values.
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters for the action.
     *
     * @param parameters a map of parameter names to their values.
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
