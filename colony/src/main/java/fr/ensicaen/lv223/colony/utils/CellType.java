/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.utils;

/**
 * Defines various types of cells that can be encountered on the planet in the
 * context of the colony simulation. Each cell type represents a different environmental
 * or resource characteristic, which is crucial for decision-making by colony robots and
 * other systems.
 * <p>
 * The enumeration includes types such as:
 * <ul>
 *   <li>{@code UNKNOWN}: For unexplored or undefined areas.</li>
 *   <li>{@code BASE}: The colony's base cell.</li>
 *   <li>{@code STONE}, {@code FOREST}, {@code DESERT}: Various terrain types.</li>
 *   <li>{@code WATER}, {@code MINERAL}: Cells containing extractable resources.</li>
 *   <li>{@code DRY_PRAIRIE}, {@code PRAIRIE}, {@code WET_PRAIRIE}: Different types of vegetative lands.</li>
 *   <li>{@code IMPENETRABLE}: Areas that are inaccessible.</li>
 *   <li>{@code FRUITS_AND_VEGETABLES}: Areas rich in consumable resources.</li>
 * </ul>
 * <p>
 * Additional methods like {@link #isExtractable()} and {@link #isProductive()} provide quick checks
 * to identify cells for resource extraction and agricultural productivity, respectively.
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
     * Determines if the cell type represents a resource that can be extracted.
     * 
     * @return {@code true} if the cell type is either {@code MINERAL} or {@code WATER},
     *         indicating that resources can be extracted; {@code false} otherwise.
     */
    public boolean isExtractable() {
        return this == MINERAL || this == WATER;
    }

    /**
     * Determines if the cell type represents a productive land area.
     * 
     * @return {@code true} if the cell type is one of the prairie types
     *         ({@code PRAIRIE}, {@code DRY_PRAIRIE}, or {@code WET_PRAIRIE}),
     *         suggesting that it can be used for agricultural activities; {@code false} otherwise.
     */
    public boolean isProductive() {
        return this == PRAIRIE || this == DRY_PRAIRIE || this == WET_PRAIRIE;
    }
}
