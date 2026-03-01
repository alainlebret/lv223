/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet.utils;

import org.junit.jupiter.api.Test;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.utils.PlanetConfigurationLoader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class PlanetConfigurationLoaderTest {
    private static final String CONFIG_FILE_PATH = "target/test-classes/json/small_planet.json";

    @Test
    void testLoadConfigurationReturnsGrid() {
        Cell[][] grid = null;
        try {
            grid = PlanetConfigurationLoader.loadConfiguration(CONFIG_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (grid == null) {
            fail("Grid is null");
            return;
        }
        assertEquals(CellType.WATER, grid[0][0].getType());
        assertEquals(CellType.STONE, grid[0][1].getType());
        assertEquals(CellType.BASE, grid[0][2].getType());
        assertEquals(CellType.DRY_PRAIRIE, grid[0][3].getType());
        assertEquals(CellType.PRAIRIE, grid[0][4].getType());
        assertEquals(CellType.WET_PRAIRIE, grid[1][0].getType());
        assertEquals(CellType.DESERT, grid[1][1].getType());
        assertEquals(CellType.FRUITS_AND_VEGETABLES, grid[1][2].getType());
        assertEquals(CellType.IMPENETRABLE, grid[1][3].getType());
        assertEquals(CellType.FOREST, grid[1][4].getType());

        assertEquals(100, grid[0][0].getUnits());
        assertEquals(0, grid[0][1].getUnits());
        assertEquals(0, grid[0][2].getUnits());
        assertEquals(0, grid[0][3].getUnits());
        assertEquals(0, grid[0][4].getUnits());
        assertEquals(0, grid[1][0].getUnits());
        assertEquals(0, grid[1][1].getUnits());
        assertEquals(70, grid[1][2].getUnits());
        assertEquals(0, grid[1][3].getUnits());
        assertEquals(90, grid[1][4].getUnits());
     
    }

}
