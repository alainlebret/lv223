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
 * Represents a cell in the colony's environment.
 * <p>
 * Each cell is defined by its location (Coordinate), type (CellType), and additional
 * dynamic data (CellData). This class is a fundamental building block of the colony's grid,
 * enabling detailed representation and manipulation of the environment in which the robots operate.
 * </p>
 * <p>
 * The cell type indicates the nature of the cell (e.g., water source, mineral deposit, or vegetation),
 * while the dynamic data may include attributes such as resource quantity or modifications due to colony activities.
 * </p>
 */
public class Cell {
    private final Coordinate coordinate;
    private CellType type;
    private CellData data;

    /**
     * Constructs a new Cell with the specified coordinate, type, and dynamic data.
     *
     * @param coordinate the coordinate of the cell
     * @param type   the type of the cell
     * @param data   the dynamic data associated with the cell
     */
    public Cell(Coordinate coordinate, CellType type, CellData data) {
        this.coordinate = coordinate;
        this.type = type;
        this.data = data;
    }

    /**
     * Returns the coordinate of this cell.
     *
     * @return the cell's coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the type of this cell.
     *
     * @return the cell type
     */
    public CellType getType() {
        return type;
    }

    /**
     * Sets the cell type.
     *
     * @param type the cell type to set
     */
    public void setType(CellType type) {
        this.type = type;
    }

    /**
     * Returns the dynamic data associated with this cell.
     *
     * @return the cell data
     */
    public CellData getData() {
        return data;
    }

    /**
     * Sets the dynamic data associated with this cell.
     *
     * @param data the cell data to set
     */
    public void setData(CellData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "coordinate=" + coordinate +
                ", cellType=" + type +
                ", cellData=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Objects.equals(coordinate, cell.coordinate) &&
               type == cell.type &&
               Objects.equals(data, cell.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, type, data);
    }
}
