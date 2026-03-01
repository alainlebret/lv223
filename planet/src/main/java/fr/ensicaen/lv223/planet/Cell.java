/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import fr.ensicaen.lv223.planet.exception.InvalidQuantityException;
import fr.ensicaen.lv223.planet.CellType;

/**
 * Represents a cell on the planet grid.
 * <p>
 * A cell holds information about the terrain type, available resources,
 * and any constructions (such as pipelines) present. It provides methods
 * for resource extraction and for triggering metamorphosis in adjacent cells.
 * </p>
 *
 * @since 1.0
 */
public class Cell {
    public static final Map<CellType, Integer> MAX_RESOURCE_UNITS = new EnumMap<>(CellType.class);

    static {
        MAX_RESOURCE_UNITS.put(CellType.MINERAL, 1000);
        MAX_RESOURCE_UNITS.put(CellType.WATER, 10000);
        MAX_RESOURCE_UNITS.put(CellType.FRUITS_AND_VEGETABLES, 1000);
        MAX_RESOURCE_UNITS.put(CellType.FOREST, 1000);
    }

    /** Random number generator for metamorphosis decisions. */
    private static final Random random = new Random();

    /** The type of terrain or environment in the cell. */
    private CellType type;

    /** The amount of resources available in the cell. */
    private int units;

    /** Flag indicating whether the cell has been visited by robots. */
    private boolean visited;

    /** Flag indicating if the cell's state has been modified. */
    private boolean modified;

    /** Indicates if an alien construction (e.g. a pipeline) is present in the cell. */
    private boolean hasAlienConstructionOnIt;

    /** The percentage of resources that have been extracted from the cell. */
    private int extractionPercentage;


    /**
     * Creates a new default cell with {@code UNKNOWN} type and zero resources.
     */
    public Cell() {
        this(CellType.UNKNOWN, 0);
    }

    /**
     * Creates a new cell with the specified type and resource units.
     *
     * @param type  the type of terrain or environment in the cell.
     * @param units the initial amount of resources available.
     */
    public Cell(CellType type, int units) {
        this.type = type;
        setUnits(units);
        this.extractionPercentage = 0;
        this.visited = false;
        this.modified = false;
        this.hasAlienConstructionOnIt = false;
    }

    // ---------------- Getters and Setters ----------------

    /**
     * Returns the cell type.
     *
     * @return the type of the cell.
     */
    public CellType getType() {
        return type;
    }

    /**
     * Sets the cell type.
     *
     * @param type the new cell type.
     */
    public void setType(CellType type) {
        this.type = type;
    }

    /**
     * Returns the resource units available in the cell.
     *
     * @return the resource units.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the resource units available in the cell.
     * <p>
     * For prairie types (DRY_PRAIRIE, WET_PRAIRIE, PRAIRIE), the logical maximum is defined
     * by the maximum units for FRUITS_AND_VEGETABLES.
     * </p>
     *
     * @param units the new resource units.
     */
    public void setUnits(int units) {
        if (this.type == CellType.DRY_PRAIRIE || this.type == CellType.WET_PRAIRIE || this.type == CellType.PRAIRIE) {
            int logicalMax = MAX_RESOURCE_UNITS.get(CellType.FRUITS_AND_VEGETABLES);
            this.units = Math.max(0, Math.min(units, logicalMax));
        } else {
            int maxUnits = MAX_RESOURCE_UNITS.getOrDefault(this.type, 0);
            this.units = Math.max(0, Math.min(units, maxUnits));
        }
    }

    /**
     * Returns the extraction percentage.
     *
     * @return the extraction percentage.
     */
    public int getExtractionPercentage() {
        return extractionPercentage;
    }

    /**
     * Sets the extraction percentage (clamped between 0 and 100).
     *
     * @param extractionPercentage the new extraction percentage.
     */
    public void setExtractionPercentage(int extractionPercentage) {
        this.extractionPercentage = Math.max(0, Math.min(extractionPercentage, 100));
    }

    /**
     * Checks whether the cell has been visited.
     *
     * @return {@code true} if visited, {@code false} otherwise.
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Sets the visited status of the cell.
     *
     * @param visited the new visited status.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Checks whether the cell has been modified.
     *
     * @return {@code true} if modified, {@code false} otherwise.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the modified status of the cell.
     *
     * @param modified the new modified status.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * Checks if an alien construction (e.g., a pipeline) exists on the cell.
     *
     * @return {@code true} if present, {@code false} otherwise.
     */
    public boolean hasAlienConstructionOnIt() {
        return hasAlienConstructionOnIt;
    }

    /**
     * Sets whether an alien construction exists on the cell.
     *
     * @param hasAlienConstructionOnIt {@code true} if present, {@code false} otherwise.
     */
    public void setHasAlienConstructionOnIt(boolean hasAlienConstructionOnIt) {
        this.hasAlienConstructionOnIt = hasAlienConstructionOnIt;
    }

    // ---------------- Cell Behavior Methods ----------------

    /**
     * Extracts resources from the cell based on a given percentage.
     * The planet is notified of this extraction.
     *
     * @param planet     the planet from which resources are extracted.
     * @param percentage the percentage of resources to extract (between 0 and 100).
     * @throws InvalidQuantityException if the percentage is out of bounds.
     */
    public void extractResources(Planet planet, int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new InvalidQuantityException("Percentage must be between 0 and 100");
        }
        this.units -= (this.units * percentage) / 100;
        planet.recordExtractionEvent(percentage);
    }

    /**
     * Triggers metamorphosis in neighboring cells within a specified radius.
     *
     * @param planet            the associated planet.
     * @param x                 the x-coordinate of the initiating cell.
     * @param y                 the y-coordinate of the initiating cell.
     * @param radius            the radius around the cell for metamorphosis.
     * @param globalProbability the global probability (seasonal or environmental) that metamorphosis occurs.
     */
    public void triggerNeighboringCellMetamorphosis(Planet planet, int x, int y, int radius, double globalProbability) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int neighborX = x + i;
                int neighborY = y + j;
                if (isValidNeighbor(planet, neighborX, neighborY)) {
                    Cell neighborCell = planet.getGrid()[neighborY][neighborX];
                    if (eligibleForMetamorphosis(neighborCell) && random.nextDouble() <= globalProbability) {
                        applyMetamorphosis(neighborCell);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the specified neighbor coordinates are within the planet's grid.
     *
     * @param planet the associated planet.
     * @param x      the x-coordinate to check.
     * @param y      the y-coordinate to check.
     * @return {@code true} if the neighbor is within bounds; {@code false} otherwise.
     */
    private boolean isValidNeighbor(Planet planet, int x, int y) {
        return x >= 0 && x < planet.getWidth() && y >= 0 && y < planet.getHeight();
    }

    /**
     * Determines if a neighbor cell is eligible for metamorphosis based on its type.
     *
     * @param neighborCell the neighbor cell to check.
     * @return {@code true} if eligible, {@code false} otherwise.
     */
    private boolean eligibleForMetamorphosis(Cell neighborCell) {
        return neighborCell.getType() != CellType.STONE &&
               neighborCell.getType() != CellType.WATER &&
               neighborCell.getType() != CellType.BASE &&
               neighborCell.getType() != CellType.IMPENETRABLE;
    }

    /**
     * Applies metamorphosis on the specified cell by determining a new cell type.
     *
     * @param cell the cell to transform.
     */
    private void applyMetamorphosis(Cell cell) {
        double probability = random.nextDouble();
        CellType newType = determineMetamorphosisType(cell.getType(), probability);
        cell.setType(newType);
        cell.setModified(true);
    }

    /**
     * Determines the new cell type based on the current type and a probability value.
     *
     * @param currentType the current cell type.
     * @param probability the probability value.
     * @return the new cell type after metamorphosis.
     */
    private CellType determineMetamorphosisType(CellType currentType, double probability) {
        switch (currentType) {
            case FOREST:
                return metamorphoseForest(probability);
            case DRY_PRAIRIE:
                return metamorphoseDryPrairie(probability);
            case PRAIRIE:
                return metamorphosePrairie(probability);
            case WET_PRAIRIE:
                return metamorphoseWetPrairie(probability);
            case FRUITS_AND_VEGETABLES:
                return metamorphoseFruitsAndVegetables(probability);
            default:
                return currentType;
        }
    }

    /**
     * Determines the new type for a FOREST cell based on probability.
     *
     * @param probability the probability value.
     * @return the new cell type.
     */
    private CellType metamorphoseForest(double probability) {
        if (probability <= 0.10) {
            return CellType.DESERT;
        } else if (probability <= 0.30) {
            return CellType.DRY_PRAIRIE;
        } else if (probability <= 0.80) {
            return CellType.PRAIRIE;
        }
        return CellType.FOREST;
    }

    /**
     * Determines the new type for a DRY_PRAIRIE cell based on probability.
     *
     * @param probability the probability value.
     * @return the new cell type.
     */
    private CellType metamorphoseDryPrairie(double probability) {
        if (probability <= 0.80) {
            return CellType.DESERT;
        }
        return CellType.DRY_PRAIRIE;
    }

    /**
     * Determines the new type for a PRAIRIE cell based on probability.
     *
     * @param probability the probability value.
     * @return the new cell type.
     */
    private CellType metamorphosePrairie(double probability) {
        if (probability <= 0.10) {
            return CellType.DESERT;
        } else if (probability <= 0.60) {
            return CellType.DRY_PRAIRIE;
        }
        return CellType.PRAIRIE;
    }

    /**
     * Determines the new type for a WET_PRAIRIE cell based on probability.
     *
     * @param probability the probability value.
     * @return the new cell type.
     */
    private CellType metamorphoseWetPrairie(double probability) {
        if (probability <= 0.05) {
            return CellType.DESERT;
        } else if (probability <= 0.30) {
            return CellType.PRAIRIE;
        } else if (probability <= 0.80) {
            return CellType.DRY_PRAIRIE;
        }
        return CellType.WET_PRAIRIE;
    }

    /**
     * Determines the new type for a FRUITS_AND_VEGETABLES cell based on probability.
     *
     * @param probability the probability value.
     * @return the new cell type.
     */
    private CellType metamorphoseFruitsAndVegetables(double probability) {
        if (probability <= 0.05) {
            return CellType.DESERT;
        } else if (probability <= 0.10) {
            return CellType.DRY_PRAIRIE;
        } else if (probability <= 0.70) {
            return CellType.PRAIRIE;
        } else if (probability <= 0.80) {
            return CellType.WET_PRAIRIE;
        }
        return CellType.FRUITS_AND_VEGETABLES;
    }
}
