/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

/**
 * Represents a "dummy" robot agent exploring or working on the planet.
 * It holds properties like type, position, and methods for robot actions.
 * <p>
 * <b>Note</b>: This class is for testing purpose only.
 * 
 * @since (lv223 simulation project)
 */
public class RobotInfo {
    /** The Id of the robot */
    private String id;

    /** The type of the robot */
    private RobotType type;

    /** The current x-coordinate of the robot */
    private int x;

    /** The current y-coordinate of the robot */
    private int y;

    /** The current injury of the robot */
    private int injury;

    /**
     * Creates a new robot with the specified type and position.
     *
     * @param id The id of the robot
     * @param type The type of the robot
     * @param x The x-coordinate of the robot
     * @param y The y-coordinate of the robot
     */
    public RobotInfo(String id, RobotType type, int x, int y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.injury = 0;
    }

    /**
     * Returns the id of the robot.
     *
     * @return the id of the robot
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the type of the robot.
     *
     * @return the type of the robot
     */
    public RobotType getType() {
        return type;
    }

    /**
     * Returns the current x-coordinate of the robot.
     *
     * @return the current x-coordinate of the robot
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current y-coordinate of the robot.
     *
     * @return the current y-coordinate of the robot
     */
    public int getY() {
        return y;
    }

     /**
     * Sets the id of the robot.
     *
     * @param id the id of the robot
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Sets the type of the robot.
     *
     * @param type the type of the robot
     */
    public void setType(RobotType type) {
        this.type = type;
    }

    /**
     * Sets the current x-coordinate of the robot.
     *
     * @param x the new x-coordinate of the robot
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the current y-coordinate of the robot.
     *
     * @param y the new y-coordinate of the robot
     */
    public void setY(int y) {
        this.y = y;
    }

    public int getInjury() {
        return injury;
    }

    public void setInjury(int injury) {
        this.injury = injury;
    }

}
