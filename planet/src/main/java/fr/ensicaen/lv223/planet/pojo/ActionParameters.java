/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.pojo;

/**
 * Plain Old Java Object (POJO) representing action parameters from clients.
 */
public class ActionParameters {
    
    private int x;
    private int y;
    private int newX;
    private int newY;
    private int units;

    /**
     * Returns the x-coordinate.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate.
     *
     * @param x the x-coordinate to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate.
     *
     * @param y the y-coordinate to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the new x-coordinate.
     *
     * @return the new x-coordinate
     */
    public int getNewX() {
        return newX;
    }

    /**
     * Sets the new x-coordinate.
     *
     * @param newX the new x-coordinate to set
     */
    public void setNewX(int newX) {
        this.newX = newX;
    }

    /**
     * Returns the new y-coordinate.
     *
     * @return the new y-coordinate
     */
    public int getNewY() {
        return newY;
    }

    /**
     * Sets the new y-coordinate.
     *
     * @param newY the new y-coordinate to set
     */
    public void setNewY(int newY) {
        this.newY = newY;
    }

    /**
     * Returns the number of units.
     *
     * @return the number of units
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of units.
     *
     * @param units the number of units to set
     */
    public void setUnits(int units) {
        this.units = units;
    }
}
