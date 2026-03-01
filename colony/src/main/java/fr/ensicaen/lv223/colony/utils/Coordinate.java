/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.utils;

import java.util.Objects;

/**
 * Represents a two-dimensional coordinate in the context of a grid or map
 * within the colony simulation. This class is fundamental for locating cells,
 * robots, and other entities within the simulation environment.
 * <p>
 * It provides basic functionality for managing and comparing coordinates,
 * including getters, setters, equality checks, and hash code generation.
 * <p>
 * The x and y properties represent the horizontal and vertical positions 
 * respectively, on the grid. This class can be utilized in various parts of 
 * the simulation where precise location mapping is required, such as robot 
 * navigation, resource management, and environmental analysis.
 */
public final class Coordinate {
    private final int x;
    private final int y;

    /**
     * Constructs a new Coordinate object with the specified x (horizontal) and y (vertical) positions.
     *
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the horizontal position.
     *
     * @return the x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the vertical position.
     *
     * @return the y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if this Coordinate object is equal to another object.
     * Two coordinates are considered equal if they have the same x and y values.
     *
     * @param obj the object to compare with.
     * @return {@code true} if the specified object is a Coordinate with the same x and y values; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    /**
     * Returns a hash code value for this Coordinate object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of this Coordinate.
     *
     * @return a string in the format "Coordinate{x=..., y=...}".
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
