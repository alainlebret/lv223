/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.utils.Config;

class CellTest {
    private static final String CONFIG_FILE_PATH = "target/test-classes/json/small_planet.json";

    private Config config = new Config();
    private Planet planet;
    private Cell unknownCell, desertCell, waterCell, forestCell, mineralCell, foodCell;

    @BeforeEach
    public void setUp() {
        config.setConfigPlanetFilePath(Paths.get(CONFIG_FILE_PATH));
        planet = new Planet(config);
        unknownCell = new Cell();
        desertCell = new Cell(CellType.DESERT, 10);
        waterCell = new Cell(CellType.WATER, 10);
        forestCell = new Cell(CellType.FOREST, 10);
        mineralCell = new Cell(CellType.MINERAL, 10);
        foodCell = new Cell(CellType.FRUITS_AND_VEGETABLES, 10);
    }

    @Test
    void testDefaultConstructor() {
        // Test case: type is UNKNOWN by default
        assertEquals(CellType.UNKNOWN, unknownCell.getType());

        // Test case: units is 0 by default
        assertEquals(0, unknownCell.getUnits());
    }

    @Test
    void testGetType() {
        CellType[][] types = new CellType[2][5];

        types[0][0] = CellType.WATER;
        types[0][1] = CellType.STONE;
        types[0][2] = CellType.BASE;
        types[0][3] = CellType.DRY_PRAIRIE;
        types[0][4] = CellType.PRAIRIE;
        types[1][0] = CellType.WET_PRAIRIE;
        types[1][1] = CellType.DESERT;
        types[1][2] = CellType.FRUITS_AND_VEGETABLES;
        types[1][3] = CellType.IMPENETRABLE;
        types[1][4] = CellType.FOREST;

        // Testing for the expected return value
        assertEquals(CellType.UNKNOWN, unknownCell.getType());
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                assertEquals(types[y][x], grid[y][x].getType());
            }
        }
    }

    @Test
    void testSetType_NullType() {
        desertCell.setType(null);
        assertNull(desertCell.getType());
    }

    @Test
    void testGetResourceQuantity() {
        int[][] units = { { 100, 0, 0, 0, 0 }, { 0, 0, 70, 0, 90 } };

        // Test case: resourceQuantity is 0
        assertEquals(0, unknownCell.getUnits());
        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                assertEquals(units[y][x], grid[y][x].getUnits());
            }
        }
    }

    @Test
    void testSetResourceQuantity() {
        // Test case: units is set within the valid range
        unknownCell.setUnits(500);
        assertEquals(0, unknownCell.getUnits());

        // Test case: units is set below the valid range
        unknownCell.setUnits(-1);
        assertEquals(0, unknownCell.getUnits());

        // Test case: units is set above the valid range
        unknownCell.setUnits(1001);
        assertEquals(0, unknownCell.getUnits());

        // Test case: units is set within the valid range
        mineralCell.setUnits(500);
        assertEquals(500, mineralCell.getUnits());

        // Test case: units is set below the valid range
        mineralCell.setUnits(-1);
        assertEquals(0, mineralCell.getUnits());

        // Test case: units is set above the valid range
        mineralCell.setUnits(1001);
        assertEquals(1000, mineralCell.getUnits());

    }

    @Test
    void testGetExtractionPercentage() {
        // Test case: extractionPercentage is 0 by default
        assertEquals(0, unknownCell.getExtractionPercentage());

        // Test case: extractionPercentage is set in the constructor
        Cell cellWithExtraction = new Cell(CellType.MINERAL, 1000);
        cellWithExtraction.setExtractionPercentage(500);
        assertEquals(100, cellWithExtraction.getExtractionPercentage());
    }

    @Test
    void testSetExtractionQuantity() {
        // Test case: extractionQuantity is set within the valid range
        unknownCell.setExtractionPercentage(50);
        assertEquals(50, unknownCell.getExtractionPercentage());

        // Test case: extractionQuantity is set below the valid range
        unknownCell.setExtractionPercentage(-1);
        assertEquals(0, unknownCell.getExtractionPercentage());

        // Test case: extractionQuantity is set above the valid range
        unknownCell.setExtractionPercentage(101);
        assertEquals(100, unknownCell.getExtractionPercentage());

        // Test case: extractionQuantity is set within the valid range
        waterCell.setExtractionPercentage(50);
        assertEquals(50, waterCell.getExtractionPercentage());

        // Test case: extractionQuantity is set below the valid range
        waterCell.setExtractionPercentage(-1);
        assertEquals(0, waterCell.getExtractionPercentage());

        // Test case: extractionQuantity is set above the valid range
        waterCell.setExtractionPercentage(101);
        assertEquals(100, waterCell.getExtractionPercentage());
    }

    @Test
    void testIsVisited() {
        // Test case: visited is false by default
        assertFalse(unknownCell.isVisited());

        // Test case: visited is set to true
        unknownCell.setVisited(true);
        assertTrue(unknownCell.isVisited());
    }

    @Test
    void testSetVisited() {
        // Test case: visited is set to true
        desertCell.setVisited(true);
        assertTrue(desertCell.isVisited());

        // Test case: visited is set to false
        desertCell.setVisited(false);
        assertFalse(desertCell.isVisited());
    }

    @Test
    void testIsModified() {
        // Test case: modified is false by default
        assertFalse(unknownCell.isModified());

        // Test case: modified is set to true
        unknownCell.setModified(true);
        assertTrue(unknownCell.isModified());
    }

    @Test
    void testSetModified() {
        // Test case: modified is set to true
        desertCell.setModified(true);
        assertTrue(desertCell.isModified());

        // Test case: modified is set to false
        desertCell.setModified(false);
        assertFalse(desertCell.isModified());
    }

}
