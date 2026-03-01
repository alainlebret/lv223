/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.pojo;

/**
 * Represents a robot that has been affected by an action.
 * <p>
 * This class holds information about a robot affected by an operation,
 * such as its identifier, type, and the level of injury sustained.
 * </p>
 */
public class AffectedRobot {
    private String id;
    private String type;
    private int injury;

    /**
     * Constructs an AffectedRobot with the specified id, type, and injury level.
     *
     * @param id     the unique identifier of the robot
     * @param type   the type of the robot (e.g., "Harvester", "Miner")
     * @param injury the injury level sustained by the robot
     */
    public AffectedRobot(String id, String type, int injury) {
        this.id = id;
        this.type = type;
        this.injury = injury;
    }

    /**
     * Default constructor that initializes the robot with empty id and type,
     * and an injury level of 0.
     */
    public AffectedRobot() {
        this("", "", 0);
    }

    /**
     * Returns the unique identifier of the robot.
     *
     * @return the robot id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the robot.
     *
     * @param id the robot id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the type of the robot.
     *
     * @return the robot type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the robot.
     *
     * @param type the robot type to set
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
     * @param injury the injury level to set
     */
    public void setInjury(int injury) {
        this.injury = injury;
    }
}
