/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.ensicaen.lv223.colony.utils.CellType;

/**
 * Represents a cell detected by a robot.
 */
public class DetectedCell {
    private CellType type;
    private int x;
    private int y;
    private int units;

    @JsonCreator
    public DetectedCell(@JsonProperty("x") int x, 
                        @JsonProperty("y") int y, 
                        @JsonProperty("type") String type, 
                        @JsonProperty("units") int units) {
        this.x = x;
        this.y = y;
        // Convert the provided string to uppercase for matching with enum constants.
        if (type == null) {
            this.type = CellType.UNKNOWN;
        } else {
            try {
                this.type = CellType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.type = CellType.UNKNOWN;
            }
        }
        this.units = units;
    }

    public DetectedCell() {
        this(0, 0, "UNKNOWN", 0);
    }

    public CellType getType() {
        return type;
    }

    // Setter that accepts a string and converts it to a CellType.
    public void setType(String type) {
        try {
            this.type = CellType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.type = CellType.UNKNOWN;
        }
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getUnits() {
        return units;
    }
    public void setUnits(int units) {
        this.units = units;
    }
}
