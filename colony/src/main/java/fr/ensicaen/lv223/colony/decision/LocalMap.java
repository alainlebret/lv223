/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.decision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ensicaen.lv223.colony.utils.Cell;
import fr.ensicaen.lv223.colony.utils.CellData;
import fr.ensicaen.lv223.colony.utils.CellType;
import fr.ensicaen.lv223.colony.utils.Coordinate;

/**
 * LocalMap is the spatial representation that each robot has of its environment.
 * It stores cell data indexed by their coordinates.
 */
public class LocalMap {

    /**
     * Map storing the cell information keyed by their coordinates.
     */
    private final Map<Coordinate, Cell> map;

    /**
     * Constructs a LocalMap with the specified base coordinate.
     * The base cell is initialized as a cell of type BASE with zero resources.
     *
     * @param baseCoordinate the base coordinate
     */
    public LocalMap(Coordinate baseCoordinate) {
        this.map = new HashMap<>();
        CellData baseCellData = new CellData(CellType.BASE, 0);
        map.put(baseCoordinate, new Cell(baseCoordinate, CellType.BASE, baseCellData));
    }

    /**
     * Updates the cell at the specified coordinate.
     *
     * @param coordinate the coordinate of the cell to update
     * @param cell       the new cell data
     */
    public void updateCell(Coordinate coordinate, Cell cell) {
        // Optionally add boundary or validity checks here.
        map.put(coordinate, cell);
    }

    /**
     * Retrieves the cell at the specified coordinate.
     * If no cell is present, returns a default cell.
     *
     * @param coordinate the coordinate of the cell to retrieve
     * @return the cell at the specified coordinate, or a default cell if not found
     */
    public Cell getCell(Coordinate coordinate) {
        Cell cell = map.get(coordinate);
        if (cell != null) {
            return cell;
        }
        return new Cell(coordinate, CellType.UNKNOWN, new CellData(CellType.UNKNOWN, 0));
    }

    /**
     * Returns an unmodifiable view of the internal cell map.
     *
     * @return an unmodifiable map of cell coordinates to cells
     */
    public Map<Coordinate, Cell> getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Updates the local map with a list of scanned cells.
     * Each scanned cell is added or replaced in the map.
     *
     * @param scannedCells the list of scanned cells to incorporate into the map
     */
    public void updateNeighborhood(List<Cell> scannedCells) {
        for (Cell cell : scannedCells) {
            updateCell(cell.getCoordinate(), cell);
        }
    }

    /**
     * Retrieves the immediate environment (neighbors) of the given location.
     * The neighborhood includes the 8 surrounding cells (diagonals and cardinals).
     *
     * @param currentLocation the current location of the robot
     * @return a list of cells representing the immediate environment
     */
    public List<Cell> getImmediateEnvironment(Coordinate currentLocation) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
            {1, 0}, {1, -1}, {0, -1}, {-1, -1}
        };

        for (int[] dir : directions) {
            Coordinate neighborCoord = new Coordinate(
                    currentLocation.getX() + dir[0],
                    currentLocation.getY() + dir[1]
            );
            neighbors.add(getCell(neighborCoord));
        }
        return neighbors;
    }
}
