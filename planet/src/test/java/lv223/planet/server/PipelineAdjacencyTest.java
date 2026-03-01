/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.server.PumpRequestHandler;
import fr.ensicaen.lv223.planet.utils.Config;
import fr.ensicaen.lv223.planet.server.PlanetServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class PipelineAdjacencyTest {
    private static final Path DEFAULT_JSON_PATH = Paths.get("target", "classes", "json", "planet2.json");

    private static PlanetServer server;
    private static Config config = new Config();
    private static Planet planet;
    private static Cell[][] grid;

    @BeforeAll
    public static void setUp() throws IOException {
        config.setConfigPlanetFilePath(DEFAULT_JSON_PATH);
        config.setPort(0); // Use a different port for test
        planet = new Planet(config);
        server = new PlanetServer(planet, config);
        grid = planet.getGrid();

        grid[0][0].setType(CellType.WATER);
        grid[0][1].setHasAlienConstructionOnIt(true);
        grid[1][0].setHasAlienConstructionOnIt(true);
        grid[1][1].setHasAlienConstructionOnIt(true);
    }

    @AfterAll
    public static void tearDown() {
        if (server != null) {
            server.stopServer();
        }
    }

    @Test
    void testIsAdjacentToPipeline() {
        // Assuming isAdjacentToPipeline is a method of PumpRequestHandler
        PumpRequestHandler handler = new PumpRequestHandler();

        // Test for true when a water cell is adjacent to a pipeline
        assertEquals(true, handler.isAdjacentToPipeline(0, 0, server));

        assertEquals(false, handler.isAdjacentToPipeline(3, 2, server));
    }
}


