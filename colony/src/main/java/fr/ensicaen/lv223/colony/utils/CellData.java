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
 * Represents detailed information about a specific cell in the colony's environment.
 * <p>
 * This class encapsulates dynamic data for a cell, including:
 * <ul>
 *   <li>Cell type (e.g. WATER, MINERAL, PRAIRIE, etc.)</li>
 *   <li>Resource quantity (units) available in the cell</li>
 *   <li>A flag indicating whether the cell has undergone metamorphosis</li>
 * </ul>
 * This information is crucial for resource management and for determining the 
 * interactions of robots with their environment.
 * </p>
 */
public class CellData {
    private CellType cellType;
    private int units; // Resource quantity (expected range: 0 to 100)
    private boolean isMetamorphosed; // Indicates if the cell has undergone metamorphosis

    /**
     * Constructs a new CellData instance with the specified cell type and resource quantity.
     *
     * @param cellType         the type of the cell
     * @param resourceQuantity the quantity of resources available in the cell
     */
    public CellData(CellType cellType, double resourceQuantity) {
        this.cellType = cellType;
        this.units = (int) Math.round(resourceQuantity);
        this.isMetamorphosed = false;
    }

    /**
     * Returns the type of the cell.
     *
     * @return the cell type
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * Returns the resource units available in the cell.
     *
     * @return the resource quantity
     */
    public int getResourceUnits() {
        return units;
    }

    /**
     * Sets the cell type.
     *
     * @param cellType the new cell type
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }
    
    /**
     * Sets the resource quantity for the cell.
     *
     * @param units the resource quantity to set
     */
    public void setResourceUnits(int units) {
        this.units = units;
    }

    /**
     * Checks whether the cell has undergone metamorphosis.
     *
     * @return true if the cell is metamorphosed, false otherwise
     */
    public boolean isMetamorphosed() {
        return isMetamorphosed;
    }

    /**
     * Sets the metamorphosis status of the cell.
     *
     * @param metamorphosed true if the cell has metamorphosed, false otherwise
     */
    public void setMetamorphosed(boolean metamorphosed) {
        this.isMetamorphosed = metamorphosed;
    }

    @Override
    public String toString() {
        return "CellData{" +
                "cellType=" + cellType +
                ", units=" + units +
                ", isMetamorphosed=" + isMetamorphosed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellData)) return false;
        CellData cellData = (CellData) o;
        return units == cellData.units &&
               isMetamorphosed == cellData.isMetamorphosed &&
               cellType == cellData.cellType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, units, isMetamorphosed);
    }
}
