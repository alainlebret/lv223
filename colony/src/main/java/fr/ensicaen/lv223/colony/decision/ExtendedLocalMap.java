/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.decision;

import fr.ensicaen.lv223.colony.robot.RobotType;
import fr.ensicaen.lv223.colony.utils.Cell;
import fr.ensicaen.lv223.colony.utils.CellData;
import fr.ensicaen.lv223.colony.utils.CellType;
import fr.ensicaen.lv223.colony.utils.Coordinate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ExtendedLocalMap is an enhanced geographical memory for a robot. It stores
 * all visited cells along with the timestamp of the last observation and a
 * reinforcement learning table for each cell. This allows the robot to not only
 * remember the terrain but also to learn which areas yield better rewards.
 */
public class ExtendedLocalMap {
    private static final Logger logger = LogManager.getLogger(ExtendedLocalMap.class);

    private RobotType robotType;

    /**
     * Internal storage mapping global coordinates to cell records.
     */
    private final Map<Coordinate, CellRecord> memory;

    /**
     * Constructs an empty ExtendedLocalMap.
     *
     * @param robotType the type of robot that owns this map
     */
    public ExtendedLocalMap(RobotType robotType) {
        this.robotType = robotType;
        this.memory = new HashMap<>();
    }

    /**
     * Records or updates the cell at the given coordinate.
     * Also updates the last seen timestamp.
     *
     * @param coordinate the global coordinate of the cell
     * @param cell       the cell data to store
     */
    public void recordCell(Coordinate coordinate, Cell cell) {
        long currentTime = System.currentTimeMillis();
        memory.put(coordinate, new CellRecord(cell, currentTime, robotType));
    }

    /**
     * Retrieves the cell record at the given coordinate.
     *
     * @param coordinate the global coordinate to retrieve
     * @return the stored cell, or a default cell if not recorded
     */
    public Cell getCell(Coordinate coordinate) {
        CellRecord record = memory.get(coordinate);
        if (record != null) {
            return record.getCell();
        }
        return new Cell(coordinate, CellType.UNKNOWN, new CellData(CellType.UNKNOWN, 0));
    }

    /**
     * Merges a batch of scanned cells into the memory.
     *
     * @param scannedCells a map of global coordinates to cells from a scan
     */
    public void mergeScanResults(Map<Coordinate, Cell> scannedCells) {
        long currentTime = System.currentTimeMillis();
        scannedCells.forEach((coord, cell) -> memory.put(coord, new CellRecord(cell, currentTime, robotType)));
    }

    /**
     * Returns an unmodifiable view of the recorded cell memory.
     *
     * @return an unmodifiable map from coordinates to cell records
     */
    public Map<Coordinate, CellRecord> getMemory() {
        return Collections.unmodifiableMap(memory);
    }

    /**
     * Returns the set of all known coordinates.
     *
     * @return a set of global coordinates
     */
    public Set<Coordinate> getKnownCoordinates() {
        return Collections.unmodifiableSet(memory.keySet());
    }

    /**
     * Represents a record for a cell in memory.
     * It includes the cell data, a timestamp of the last observation,
     * and a Q-table for reinforcement learning.
     * <p>
     * The Q-table maps action names (e.g., "move", "scan", "harvest") to their
     * estimated values. You can use this to implement Q-learning or other RL
     * algorithms in your robot strategies.
     * </p>
     */
    public static class CellRecord {
        private final Cell cell;
        private long lastSeenTimestamp;
        private final Map<String, Double> qValues;

        public CellRecord(Cell cell, long timestamp, RobotType robotType) {
            this.cell = cell;
            this.lastSeenTimestamp = timestamp;
            this.qValues = new HashMap<>();
            // TODO: Initialize Q-values with the actions relevant to your robot type.
            // For example, a Harvester might have actions: "scan", "move", "harvest".
            // A Miner might have: "scan", "move", "mine".
        }

        public Cell getCell() {
            return cell;
        }

        public long getLastSeenTimestamp() {
            return lastSeenTimestamp;
        }

        public void updateTimestamp(long timestamp) {
            this.lastSeenTimestamp = timestamp;
        }

        public Map<String, Double> getQValues() {
            return Collections.unmodifiableMap(qValues);
        }

       /**
        * Updates the Q-value for the given action.
        * <p>
        * TODO: Implement a Q-learning update rule, e.g.:
        * {@code Q(s,a) = Q(s,a) + alpha * (reward + gamma * max Q(s',a') - Q(s,a))}
        * </p>
        *
        * @param action         the action taken
        * @param reward         the reward received
        * @param learningRate   the learning rate (alpha)
        * @param discountFactor the discount factor (gamma)
        */
        public void updateQValue(String action, double reward, double learningRate, double discountFactor) {
            // TODO: Implement Q-value update.
        }
    }

}
