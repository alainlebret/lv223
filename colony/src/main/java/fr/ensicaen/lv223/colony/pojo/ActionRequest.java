/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.pojo;

import java.util.Collections;
import java.util.Map;

/**
 * A Plain Old Java Object (POJO) representing an action request from a client.
 * <p>
 * This class encapsulates the details of an action request including the type of action,
 * the identifier and type of the robot performing the action, and any additional parameters.
 * </p>
 */
public class ActionRequest {
    private String action;
    private String robotId;
    private String robotType;
    private Map<String, Object> parameters;

    /**
     * Default constructor initializing fields with default values.
     * The parameters map is initialized as an empty map to avoid null pointer issues.
     */
    public ActionRequest() {
        this.action = "";
        this.robotId = "";
        this.robotType = "";
        this.parameters = Collections.emptyMap();
    }

    /**
     * Constructs an ActionRequest with the specified details.
     *
     * @param action     the action name (e.g., "move", "harvest", etc.)
     * @param robotId    the unique identifier of the robot
     * @param robotType  the type of the robot (e.g., "Harvester")
     * @param parameters a map of additional parameters for the action
     */
    public ActionRequest(String action, String robotId, String robotType, Map<String, Object> parameters) {
        this.action = action;
        this.robotId = robotId;
        this.robotType = robotType;
        this.parameters = parameters;
    }

    /**
     * Returns the action name.
     *
     * @return the action name
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action name.
     *
     * @param action the action name to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Returns the robot identifier.
     *
     * @return the robot identifier
     */
    public String getRobotId() {
        return robotId;
    }

    /**
     * Sets the robot identifier.
     *
     * @param robotId the robot identifier to set
     */
    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    /**
     * Returns the robot type.
     *
     * @return the robot type
     */
    public String getRobotType() {
        return robotType;
    }

    /**
     * Sets the robot type.
     *
     * @param robotType the robot type to set
     */
    public void setRobotType(String robotType) {
        this.robotType = robotType;
    }

    /**
     * Returns the parameters map.
     *
     * @return the parameters map
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters map.
     *
     * @param parameters the parameters map to set
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
