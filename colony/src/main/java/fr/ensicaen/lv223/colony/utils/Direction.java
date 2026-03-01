/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumerates the possible directions of movement in the simulation environment.
 * It includes the cardinal directions (NORTH, SOUTH, EAST, WEST) and the diagonal
 * directions (NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST), as well as a NONE direction.
 * <p>
 * This enumeration is particularly useful for specifying movement or orientation of
 * robots and other entities within the simulation. It provides a clear and standardized
 * way of describing directionality in various aspects of the simulation logic such as navigation,
 * resource gathering, and exploration.
 * <p>
 * Each direction is associated with a horizontal (deltaX) and vertical (deltaY) offset.
 * 
 * @author Alain Lebret (original version)
 * @author Hugo Helluy (enhancement adding delta X and Y)
 * @version 1.1 (2025-02-19)
 */
public enum Direction {
    NONE(0, 0),
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0),
    NORTHEAST(1, -1),
    SOUTHEAST(1, 1),
    SOUTHWEST(-1, 1),
    NORTHWEST(-1, -1);

    ;

    /** All directions except NONE, suitable for random movement selection. */
    public static final List<Direction> MOVABLE = Collections.unmodifiableList(
            Arrays.stream(values()).filter(d -> d != NONE).collect(Collectors.toList())
    );

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Returns the horizontal offset associated with this direction.
     *
     * @return the deltaX value.
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Returns the vertical offset associated with this direction.
     *
     * @return the deltaY value.
     */
    public int getDeltaY() {
        return deltaY;
    }

    // Optional: add utility methods if needed, for example:
    // /**
    //  * Returns the opposite direction.
    //  *
    //  * @return the opposite direction.
    //  */
    // public Direction opposite() {
    //     switch (this) {
    //         case NORTH: return SOUTH;
    //         case SOUTH: return NORTH;
    //         case EAST: return WEST;
    //         case WEST: return EAST;
    //         case NORTHEAST: return SOUTHWEST;
    //         case NORTHWEST: return SOUTHEAST;
    //         case SOUTHEAST: return NORTHWEST;
    //         case SOUTHWEST: return NORTHEAST;
    //         default: return NONE;
    //     }
    // }
}
