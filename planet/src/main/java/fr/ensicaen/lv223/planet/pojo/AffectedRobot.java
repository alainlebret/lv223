/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.pojo;

/**
 * Plain Old Java Object (POJO) representing a robot that has been affected by an action.
 * This class encapsulates details about a robot including its identifier, type, and injury level.
 */
public class AffectedRobot {
    
    /** The unique identifier of the robot. */
    private String id;
    
    /** The type of the robot (e.g., "Harvester", "Miner"). */
    private String type;
    
    /** The injury level of the robot (0 indicates no injury). */
    private int injury;

    /**
     * Constructs an AffectedRobot with the specified identifier, type, and injury level.
     *
     * @param id     the unique identifier of the robot
     * @param type   the type of the robot
     * @param injury the injury level of the robot
     */
    public AffectedRobot(String id, String type, int injury) {
        this.id = id;
        this.type = type;
        this.injury = injury;
    }

    /**
     * Returns the identifier of the robot.
     *
     * @return the robot's id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the robot.
     *
     * @param id the new identifier for the robot
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the type of the robot.
     *
     * @return the robot's type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the robot.
     *
     * @param type the new type for the robot
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the injury level of the robot.
     *
     * @return the injury level
     */
    public int getInjury() {
        return injury;
    }

    /**
     * Sets the injury level of the robot.
     *
     * @param injury the new injury level
     */
    public void setInjury(int injury) {
        this.injury = injury;
    }
}
