/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.pojo;

import fr.ensicaen.lv223.planet.CellType;

/**
 * Plain Old Java Object (POJO) representing a cell detected by a robot during a scan action.
 * <p>
 * This class encapsulates details about a cell, including its position (x, y), type,
 * and the number of resource units detected.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DetectedCell {

    /** The type of the cell. */
    private CellType type;

    /** The x-coordinate of the cell. */
    private int x;

    /** The y-coordinate of the cell. */
    private int y;

    /** The number of resource units in the cell. */
    private int units;

    /**
     * Constructs a DetectedCell with the specified coordinates, type, and units.
     *
     * @param x     the x-coordinate of the cell
     * @param y     the y-coordinate of the cell
     * @param type  the type of the cell
     * @param units the number of resource units detected
     */
    public DetectedCell(int x, int y, CellType type, int units) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.units = units;
    }

    /**
     * Constructs a DetectedCell with the specified coordinates.
     * The cell type is set to {@code CellType.UNKNOWN} and units to 0.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     */
    public DetectedCell(int x, int y) {
        this(x, y, CellType.UNKNOWN, 0);
    }

    /**
     * Returns the x-coordinate of the cell.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the cell.
     *
     * @param x the new x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the cell.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the cell.
     *
     * @param y the new y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the type of the cell.
     *
     * @return the cell type
     */
    public CellType getType() {
        return type;
    }

    /**
     * Sets the type of the cell.
     *
     * @param type the new cell type
     */
    public void setType(CellType type) {
        this.type = type;
    }

    /**
     * Returns the number of resource units in the cell.
     *
     * @return the resource units
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the number of resource units in the cell.
     *
     * @param units the new number of resource units
     */
    public void setUnits(int units) {
        this.units = units;
    }
}
