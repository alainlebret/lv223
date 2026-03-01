/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.Map;

import fr.ensicaen.lv223.planet.pojo.ActionRequest;

/**
 * Represents the context for an action request received from a robot.
 * This context encapsulates the {@code ActionRequest} along with a reference
 * to the {@code PlanetServer} processing the request.
 *
 * <p>
 * This class provides convenient access to common request properties such as
 * the action name, robot identifier, robot type, and any associated parameters.
 * </p>
 *
 * @since 1.0
 */
public class RequestContext {
    
    /** The action request received from the client. */
    private final ActionRequest actionRequest;
    
    /** The planet server handling the request. */
    private final PlanetServer server;

    /**
     * Constructs a new {@code RequestContext} with the specified action request and server.
     *
     * @param actionRequest the action request; must not be {@code null}
     * @param server the planet server; must not be {@code null}
     * @throws IllegalArgumentException if either parameter is {@code null}
     */
    public RequestContext(ActionRequest actionRequest, PlanetServer server) {
        if (actionRequest == null || server == null) {
            throw new IllegalArgumentException("ActionRequest and PlanetServer cannot be null");
        }
        this.actionRequest = actionRequest;
        this.server = server;
    }

    /**
     * Returns the action request.
     *
     * @return the {@code ActionRequest} associated with this context
     */
    public ActionRequest getActionRequest() {
        return actionRequest;
    }

    /**
     * Returns the planet server handling the request.
     *
     * @return the {@code PlanetServer} associated with this context
     */
    public PlanetServer getServer() {
        return server;
    }

    /**
     * Returns the action name from the request.
     *
     * @return the action name as a {@code String}
     */
    public String getAction() {
        return actionRequest.getAction();
    }

    /**
     * Returns the identifier of the robot that initiated the request.
     *
     * @return the robot identifier as a {@code String}
     */
    public String getRobotId() {
        return actionRequest.getRobotId();
    }

    /**
     * Returns the type of the robot that initiated the request.
     *
     * @return the robot type as a {@code String}
     */
    public String getRobotType() {
        return actionRequest.getRobotType();
    }

    /**
     * Returns the parameters associated with the action request.
     *
     * @return a map of parameter names to their values
     */
    public Map<String, Object> getParameters() {
        return actionRequest.getParameters();
    }
}
