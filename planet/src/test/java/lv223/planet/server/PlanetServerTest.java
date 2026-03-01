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
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;
import fr.ensicaen.lv223.planet.utils.Config;
import fr.ensicaen.lv223.planet.server.PlanetServer;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

class PlanetServerTest {
        public static final Path DEFAULT_JSON_PATH = Paths.get("target", "classes", "json", "planet2.json");

        private static Config config = new Config();
        private static Planet planet;
        private static PlanetServer server;

        RobotType[] types = { RobotType.CARTOGRAPHER, RobotType.FARMER, RobotType.MINER, RobotType.HARVESTER,
                        RobotType.PIPELINER };

        @BeforeAll
        public static void setUp() throws IOException {
                config.setConfigPlanetFilePath(DEFAULT_JSON_PATH);
                config.setPort(0);
                planet = new Planet(config);
                server = new PlanetServer(planet, config);
        }

        @AfterAll
        public static void setDown() throws IOException {
        }       

        @Test
        void testAnswerToScanRequest() throws Exception {
//                planet = new Planet(config);

                String request;
                String response;
                Cell[][] grid = planet.getGrid();
                server.getColonyRobots().clear();

//                { "TAGADATSOINTSOIN", "{\"status\":\"error\",\"message\":\"Invalid request format\"}" },

                String[][] testCases = {
                                { "TAGADATSOINTSOIN", "{\"status\":\"error\",\"message\":\"Invalid JSON format\"}" },
                                { createScanRequest("C1", "Cartographer", -1, -1),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C1\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("C2", "Cartographer", 200, 200),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C2\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("C3", "Cartografer", -1, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid robot type\",\"affectedRobots\":[{\"id\":\"C3\",\"type\":\"Cartografer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("C4", "Cartographer", 10, -1),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C4\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("C5", "Cartographer", 20, 0),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C5\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"UNKNOWN\",\"x\":19,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":20,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":-1,\"units\":0},{\"type\":\"MINERAL\",\"x\":19,\"y\":0,\"units\":1000},{\"type\":\"STONE\",\"x\":20,\"y\":0,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":0,\"units\":0},{\"type\":\"STONE\",\"x\":19,\"y\":1,\"units\":0},{\"type\":\"STONE\",\"x\":20,\"y\":1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":1,\"units\":0}]}" },
                                { createScanRequest("C6", "Cartographer", 4, 0),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C6\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"UNKNOWN\",\"x\":3,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":4,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":5,\"y\":-1,\"units\":0},{\"type\":\"WATER\",\"x\":3,\"y\":0,\"units\":9990},{\"type\":\"DRY_PRAIRIE\",\"x\":4,\"y\":0,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":5,\"y\":0,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":3,\"y\":1,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":4,\"y\":1,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":5,\"y\":1,\"units\":0}]}" },
                                { createScanRequest("C7", "Cartographer", 17, 2),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C7\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"DRY_PRAIRIE\",\"x\":16,\"y\":1,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":17,\"y\":1,\"units\":0},{\"type\":\"FRUITS_AND_VEGETABLES\",\"x\":18,\"y\":1,\"units\":1000},{\"type\":\"WET_PRAIRIE\",\"x\":16,\"y\":2,\"units\":0},{\"type\":\"PRAIRIE\",\"x\":17,\"y\":2,\"units\":0},{\"type\":\"STONE\",\"x\":18,\"y\":2,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":16,\"y\":3,\"units\":0},{\"type\":\"PRAIRIE\",\"x\":17,\"y\":3,\"units\":0},{\"type\":\"STONE\",\"x\":18,\"y\":3,\"units\":0}]}" },
                                { createScanRequest("C8", "Cartographer", 5, 0),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C8\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"UNKNOWN\",\"x\":4,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":5,\"y\":-1,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":6,\"y\":-1,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":4,\"y\":0,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":5,\"y\":0,\"units\":0},{\"type\":\"DESERT\",\"x\":6,\"y\":0,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":4,\"y\":1,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":5,\"y\":1,\"units\":0},{\"type\":\"DESERT\",\"x\":6,\"y\":1,\"units\":0}]}" },
                                { createScanRequest("C9", "Cartographer", 10, 10),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C9\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"WET_PRAIRIE\",\"x\":9,\"y\":9,\"units\":0},{\"type\":\"STONE\",\"x\":10,\"y\":9,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":11,\"y\":9,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":9,\"y\":10,\"units\":0},{\"type\":\"BASE\",\"x\":10,\"y\":10,\"units\":0},{\"type\":\"STONE\",\"x\":11,\"y\":10,\"units\":0},{\"type\":\"WATER\",\"x\":9,\"y\":11,\"units\":10000},{\"type\":\"STONE\",\"x\":10,\"y\":11,\"units\":0},{\"type\":\"STONE\",\"x\":11,\"y\":11,\"units\":0}]}" },
                                { createScanRequest("C10", "Cartographer", 17, 8),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C10\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"STONE\",\"x\":16,\"y\":7,\"units\":0},{\"type\":\"WATER\",\"x\":17,\"y\":7,\"units\":10000},{\"type\":\"PRAIRIE\",\"x\":18,\"y\":7,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":16,\"y\":8,\"units\":0},{\"type\":\"FRUITS_AND_VEGETABLES\",\"x\":17,\"y\":8,\"units\":1000},{\"type\":\"WET_PRAIRIE\",\"x\":18,\"y\":8,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":16,\"y\":9,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":17,\"y\":9,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":18,\"y\":9,\"units\":0}]}" },
                                { createScanRequest("C11", "Cartographer", 6, 10),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C11\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"DRY_PRAIRIE\",\"x\":5,\"y\":9,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":6,\"y\":9,\"units\":0},{\"type\":\"STONE\",\"x\":7,\"y\":9,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":5,\"y\":10,\"units\":0},{\"type\":\"IMPENETRABLE\",\"x\":6,\"y\":10,\"units\":0},{\"type\":\"STONE\",\"x\":7,\"y\":10,\"units\":0},{\"type\":\"WET_PRAIRIE\",\"x\":5,\"y\":11,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":6,\"y\":11,\"units\":0},{\"type\":\"DRY_PRAIRIE\",\"x\":7,\"y\":11,\"units\":0}]}" },
                                { createScanRequest("F1", "Farmer", 6, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"F1\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("M1", "Miner", 6, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"M1\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("H1", "Harvester", 6, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"H1\",\"type\":\"Harvester\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("P1", "Pipeliner", 6, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"P1\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("C12", "Cartographer", 20, 10),
                                                "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C12\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[{\"type\":\"PRAIRIE\",\"x\":19,\"y\":9,\"units\":0},{\"type\":\"PRAIRIE\",\"x\":20,\"y\":9,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":9,\"units\":0},{\"type\":\"WATER\",\"x\":19,\"y\":10,\"units\":10000},{\"type\":\"WATER\",\"x\":20,\"y\":10,\"units\":10000},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":10,\"units\":0},{\"type\":\"WATER\",\"x\":19,\"y\":11,\"units\":10000},{\"type\":\"PRAIRIE\",\"x\":20,\"y\":11,\"units\":0},{\"type\":\"UNKNOWN\",\"x\":21,\"y\":11,\"units\":0}]}" },
                                { createScanRequest("F2", "Farmer", 20, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"F2\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("M2", "Miner", 20, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"M2\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("H2", "Harvester", 20, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"H2\",\"type\":\"Harvester\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createScanRequest("P2", "Pipeliner", 20, 10),
                                                "{\"status\":\"error\",\"action\":\"scan\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"P2\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                };

                for (int i = 0; i < testCases.length; i++) {
                        request = testCases[i][0];
                        response = server.answerToRequest(request);
                        assertEquals(testCases[i][1], response, "" + i);
                }

                resetAlienConstructionOnValidCells(planet);
                server.getColonyRobots().clear();
        }

        @Test
        void testAnswerToMoveRequest() throws Exception {
                String request;
                String response;
                Cell[][] grid = planet.getGrid();

                server.getColonyRobots().put("C13", new RobotInfo("C13", RobotType.CARTOGRAPHER, 11, 14));
                server.getColonyRobots().put("C14", new RobotInfo("C14", RobotType.CARTOGRAPHER, 11, 13));
                server.getColonyRobots().put("C15", new RobotInfo("C15", RobotType.CARTOGRAPHER, 11, 14));
                server.getColonyRobots().put("C16", new RobotInfo("C16", RobotType.CARTOGRAPHER, 11, 13));
                server.getColonyRobots().put("C17", new RobotInfo("C17", RobotType.CARTOGRAPHER, 11, 13));
                server.getColonyRobots().put("C18", new RobotInfo("C18", RobotType.CARTOGRAPHER, 11, 13));
                server.getColonyRobots().put("F3", new RobotInfo("F3", RobotType.FARMER, 11, 13));
                server.getColonyRobots().put("M3", new RobotInfo("M3", RobotType.MINER, 11, 13));
                server.getColonyRobots().put("H3", new RobotInfo("H3", RobotType.HARVESTER, 11, 13));
                server.getColonyRobots().put("P3", new RobotInfo("P3", RobotType.PIPELINER, 11, 13));

                String[][] testCases = {
                                { createMoveRequest("C13", "Cartographer", -1, -1, 0, 0),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C13\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("C14", "Cartographer", 0, 0, -1, -1),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C14\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("C15", "Cartographer", 0, 0, 200, 200),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"C15\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("C16", "Cartographer", 10, 10, 10, 11),
                                                "{\"status\":\"success\",\"action\":\"move\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C16\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("C17", "Cartographer", 0, 0, 15, 15),
                                                "{\"status\":\"success\",\"action\":\"move\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C17\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("C18", "Cartographer", 10, 10, 0, 0),
                                                "{\"status\":\"success\",\"action\":\"move\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C18\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("F4", "Farmer", 11, 13, 6, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"F4\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("M4", "Miner", 11, 13, 6, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"M4\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("H4", "Farmer", 11, 13, 6, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"H4\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("P4", "Farmer", 11, 13, 6, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"P4\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("F5", "Farmer", 11, 13, 20, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"F5\",\"type\":\"Farmer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("M5", "Miner", 11, 13, 20, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"M5\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("H5", "Harvester", 11, 13, 20, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"H5\",\"type\":\"Harvester\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMoveRequest("P5", "Pipeliner", 11, 13, 20, 10),
                                                "{\"status\":\"error\",\"action\":\"move\",\"message\":\"Invalid destination cell\",\"affectedRobots\":[{\"id\":\"P5\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" }
                };

                for (String[] testCase : testCases) {
                        request = testCase[0];
                        response = server.answerToRequest(request);
                        assertEquals(testCase[1], response);
                }

                resetAlienConstructionOnValidCells(planet);
                server.getColonyRobots().clear();
        }

        @Test
        void testAnswerToMineRequest() throws Exception {
                String request;
                String response;
                Cell[][] grid = planet.getGrid();

                server.getColonyRobots().put("M6", new RobotInfo("M6", RobotType.MINER, 11, 14));
                server.getColonyRobots().put("M7", new RobotInfo("M7", RobotType.MINER, 11, 13));
                server.getColonyRobots().put("M8", new RobotInfo("M8", RobotType.MINER, 11, 14));
                server.getColonyRobots().put("C19", new RobotInfo("C19", RobotType.CARTOGRAPHER, 11, 13));

                String[][] testCases = {
                                { createMineRequest("M6", "Miner", 1, 14, 0),
                                                "{\"status\":\"error\",\"action\":\"mine\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"M6\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("C19", "Cartographer", 11, 14, 10),
                                                "{\"status\":\"error\",\"action\":\"mine\",\"message\":\"Invalid robot type\",\"affectedRobots\":[{\"id\":\"C19\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("M7", "Miner", 11, 14, -10),
                                                "{\"status\":\"error\",\"action\":\"mine\",\"message\":\"Invalid quantity\",\"affectedRobots\":[{\"id\":\"M7\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("M8", "Miner", 11, 14, 101),
                                                "{\"status\":\"error\",\"action\":\"mine\",\"message\":\"Invalid quantity\",\"affectedRobots\":[{\"id\":\"M8\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("M6", "Miner", 11, 14, 10),
                                                "{\"status\":\"success\",\"action\":\"mine\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"M6\",\"type\":\"MINER\",\"injury\":0},{\"id\":\"M7\",\"type\":\"MINER\",\"injury\":8},{\"id\":\"C19\",\"type\":\"CARTOGRAPHER\",\"injury\":0},{\"id\":\"M8\",\"type\":\"MINER\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("M7", "Miner", 11, 13, 90),
                                                "{\"status\":\"error\",\"action\":\"mine\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"M7\",\"type\":\"Miner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createMineRequest("M8", "Miner", 11, 14, 50),
                                                "{\"status\":\"success\",\"action\":\"mine\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"M6\",\"type\":\"MINER\",\"injury\":0},{\"id\":\"M7\",\"type\":\"MINER\",\"injury\":8},{\"id\":\"C19\",\"type\":\"CARTOGRAPHER\",\"injury\":0},{\"id\":\"M8\",\"type\":\"MINER\",\"injury\":0}],\"detectedCells\":[]}" },
                };

                for (int i = 0; i < testCases.length; i++) {
                        request = testCases[i][0];
                        response = server.answerToRequest(request);
                        assertEquals(testCases[i][1], response);
                        if (i == 2) {
                                assertEquals(1000, grid[14][11].getUnits(), "(" + i + ")");
                        }
                        if (i == 3) {
                                assertEquals(1000, grid[14][11].getUnits(), "(" + i + ")");
                        }
                }

                resetAlienConstructionOnValidCells(planet);
                server.getColonyRobots().clear();
        }

        @Test
        void testPump() throws Exception {
                String request;
                String response;
                Cell[][] grid = planet.getGrid();

                server.getColonyRobots().put("P6", new RobotInfo("P6", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P7", new RobotInfo("P7", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P8", new RobotInfo("P8", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P9", new RobotInfo("P9", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P10", new RobotInfo("P10", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P11", new RobotInfo("P11", RobotType.PIPELINER, 11, 13));
                server.getColonyRobots().put("P12", new RobotInfo("P12", RobotType.PIPELINER, 11, 13));
                String[][] testCases = {
                                { createPumpRequest("P6", "Pipelette", 3, 0, 0),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid robot type\",\"affectedRobots\":[{\"id\":\"P6\",\"type\":\"Pipelette\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P7", "Pipeliner", 3, 0, 10),
                                                "{\"status\":\"success\",\"action\":\"pump\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"P7\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P8", "Pipeliner", -1, -1, 0),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"P8\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P9", "Pipeliner", 3, 0, -10),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid quantity\",\"affectedRobots\":[{\"id\":\"P9\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P10", "Pipeliner", 3, 0, 10000),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid quantity\",\"affectedRobots\":[{\"id\":\"P10\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P11", "Pipeliner", 2, 1, 90),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"P11\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                                { createPumpRequest("P12", "Pipeliner", 2, 2, 90),
                                                "{\"status\":\"error\",\"action\":\"pump\",\"message\":\"Invalid cell\",\"affectedRobots\":[{\"id\":\"P12\",\"type\":\"Pipeliner\",\"injury\":0}],\"detectedCells\":[]}" },
                };

                // Construct a pipeline network first
                setAlienConstructionOnValidCells(planet);

                for (int i = 0; i < testCases.length; i++) {
                        request = testCases[i][0];
                        response = server.answerToRequest(request);
                        assertEquals(testCases[i][1], response);
                        if (i == 1) {
                                assertEquals(9990, grid[0][3].getUnits(), "(" + i + ")");
                        }
                        if (i == 5) {
                                assertEquals(9990, grid[0][3].getUnits(), "(" + i + ")");
                        }
                }
                resetAlienConstructionOnValidCells(planet);
                server.getColonyRobots().clear();
        }

        /**
         * Sets alien construction (pipeline) on all valid cells in the planet grid.
         * Valid cells are those that are not water or impenetrable.
         * 
         * @param planet The planet whose cells are to be updated.
         */
        private void setAlienConstructionOnValidCells(Planet planet) {
                Cell[][] grid = planet.getGrid();
                for (int y = 0; y < grid.length; y++) {
                        for (int x = 0; x < grid[y].length; x++) {
                                Cell cell = grid[y][x];
                                // Check if the cell is neither water nor impenetrable
                                if (cell.getType() != CellType.WATER && cell.getType() != CellType.IMPENETRABLE) {
                                        cell.setHasAlienConstructionOnIt(true);
                                }
                        }
                }
        }

        /**
         * Resets alien construction (pipeline) on all valid cells in the planet grid.
         * Valid cells are those that are not water or impenetrable.
         * 
         * @param planet The planet whose cells are to be updated.
         */
        private void resetAlienConstructionOnValidCells(Planet planet) {
                Cell[][] grid = planet.getGrid();
                for (int y = 0; y < grid.length; y++) {
                        for (int x = 0; x < grid[y].length; x++) {
                                Cell cell = grid[y][x];
                                // Check if the cell is neither water nor impenetrable
                                if (cell.getType() != CellType.WATER && cell.getType() != CellType.IMPENETRABLE) {
                                        cell.setHasAlienConstructionOnIt(true);
                                }
                        }
                }
        }

        private String createMoveRequest(String id, String type, int x, int y, int newX, int newY) {
                return "{ \"action\": \"move\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + ",\"newX\":" + newX
                                + ", \"newY\":" + newY
                                + " } }";
        }

        private String createScanRequest(String id, String type, int x, int y) {
                return "{ \"action\": \"scan\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + " } }";
        }

        private String createCultivateRequest(String id, String type, int x, int y) {
                return "{ \"action\": \"cultivate\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + " } }";
        }

        private String createHarvestRequest(String id, String type, int x, int y, int units) {
                return "{ \"action\": \"harvest\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + ", \"units\":" + units + " } }";
        }

        private String createPipeRequest(String id, String type, int x, int y) {
                return "{ \"action\": \"pipe\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + " } }";
        }

        private String createMineRequest(String id, String type, int x, int y, int units) {
                return "{ \"action\": \"mine\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + ", \"units\":" + units + " } }";
        }

        private String createPumpRequest(String id, String type, int x, int y, int units) {
                return "{ \"action\": \"pump\", \"robotId\":\"" + id + "\",\"robotType\": \"" + type
                                + "\", \"parameters\": { \"x\":" + x + ", \"y\":" + y + ", \"units\":" + units + " } }";
        }

}
