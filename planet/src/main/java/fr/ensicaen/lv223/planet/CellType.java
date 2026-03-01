/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet;

/**
 * Defines various terrain or environment types in the planet grid for the 
 * lv223 simulation project. Each {@code CellType} represents unique 
 * characteristics, contributing to the diversity and complexity of the 
 * planetary ecosystem.
 * <p>
 * This enumeration is used to specify the terrain type of each cell in the
 * simulation grid, influencing its behavior and interactions within the 
 * simulation.
 * </p>
 * 
 * <p><b>Cell Types include:</b></p>
 * <ul>
 *   <li>{@code UNKNOWN} - Undefined terrain.</li>
 *   <li>{@code BASE} - The base cell with the robot colony.</li>
 *   <li>{@code STONE} - Rocky or barren terrain.</li>
 *   <li>{@code FOREST} - Wooded areas rich in flora.</li>
 *   <li>{@code DESERT} - Arid, sandy terrain without resources.</li>
 *   <li>{@code WATER} - Bodies of water like lakes or rivers.</li>
 *   <li>{@code MINERAL} - Areas rich in mineral resources.</li>
 *   <li>{@code DRY_PRAIRIE} - Dry grassland that requires abundant water for cultivation.</li>
 *   <li>{@code PRAIRIE} - Typical grassland that requires moderate water for cultivation.</li>
 *   <li>{@code WET_PRAIRIE} - Moist grassland that requires less water for cultivation.</li>
 *   <li>{@code IMPENETRABLE} - Inaccessible terrain.</li>
 *   <li>{@code FRUITS_AND_VEGETABLES} - Areas suitable for crop harvesting.</li>
 * </ul>
 * 
 * <p><b>Example usage:</b></p>
 * <pre>
 *     Cell cell = new Cell(CellType.FOREST, 100); // Creates a forest cell.
 * </pre>
 *
 * @since 1.0
 */
public enum CellType {
    UNKNOWN,
    BASE,
    STONE,
    FOREST,
    DESERT,
    WATER,
    MINERAL,
    DRY_PRAIRIE,
    PRAIRIE,
    WET_PRAIRIE,
    IMPENETRABLE,
    FRUITS_AND_VEGETABLES;

    /**
     * Determines whether resources can be extracted from a cell of this type.
     *
     * @return {@code true} if the cell is extractable (e.g., MINERAL or WATER), {@code false} otherwise.
     */
    public boolean isExtractable() {
        return this == MINERAL || this == WATER;
    }

    /**
     * Determines whether a cell of this type is suitable for cultivation.
     *
     * @return {@code true} if the cell is cultivable (e.g., prairie types or crops), {@code false} otherwise.
     */
    public boolean isCultivable() {
        return this == DRY_PRAIRIE || this == PRAIRIE || this == WET_PRAIRIE || this == FRUITS_AND_VEGETABLES;
    }
}
