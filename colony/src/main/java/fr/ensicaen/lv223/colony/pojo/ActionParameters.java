/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.pojo;

/**
 * A Plain Old Java Object (POJO) representing action parameters received from clients.
 * <p>
 * This class encapsulates the parameters required for various action requests, such as
 * movement and resource operations.
 * </p>
 */
public class ActionParameters {
    private int x;
    private int y;
    private int newX;
    private int newY;
    private int units;

    // --- Getters and Setters ---

    /**
     * Gets the current x-coordinate.
     *
     * @return the current x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the current x-coordinate.
     *
     * @param x the current x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the current y-coordinate.
     *
     * @return the current y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the current y-coordinate.
     *
     * @param y the current y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the new x-coordinate.
     *
     * @return the new x-coordinate
     */
    public int getNewX() {
        return newX;
    }

    /**
     * Sets the new x-coordinate.
     *
     * @param newX the new x-coordinate
     */
    public void setNewX(int newX) {
        this.newX = newX;
    }

    /**
     * Gets the new y-coordinate.
     *
     * @return the new y-coordinate
     */
    public int getNewY() {
        return newY;
    }

    /**
     * Sets the new y-coordinate.
     *
     * @param newY the new y-coordinate
     */
    public void setNewY(int newY) {
        this.newY = newY;
    }

    /**
     * Gets the number of units.
     *
     * @return the number of units
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of units.
     *
     * @param units the number of units
     */
    public void setUnits(int units) {
        this.units = units;
    }
}
