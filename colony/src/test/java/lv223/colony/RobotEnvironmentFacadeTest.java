/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.colony;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.ensicaen.lv223.colony.communication.PlanetServerConnection;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.robot.Cartographer;
import fr.ensicaen.lv223.colony.robot.Harvester;
import fr.ensicaen.lv223.colony.utils.Cell;
import fr.ensicaen.lv223.colony.utils.CellType;
import fr.ensicaen.lv223.colony.utils.Coordinate;

class RobotEnvironmentFacadeTest {

    /**
     * Tests that a scan request is built, sent, and the response is processed
     * correctly.
     */
    @Test
    void testScan() {
        // Arrange
        PlanetServerConnection mockConnection = Mockito.mock(PlanetServerConnection.class);
        String scanResponseJson = "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"Scan complete\","
                + "\"affectedRobots\":[],"
                + "\"detectedCells\":["
                + "  {\"x\":1,\"y\":1,\"type\":\"forest\",\"units\":50},"
                + "  {\"x\":2,\"y\":2,\"type\":\"water\",\"units\":100}"
                + "]}";
        when(mockConnection.sendRequest(anyString())).thenReturn(scanResponseJson);
    
        // Create the facade with the mock connection and base at (0,0)
        RobotEnvironmentFacade facade = new RobotEnvironmentFacade(mockConnection, 0, 0);
    
        // Create a Cartographer robot at (0,0)
        Cartographer robot = new Cartographer(facade);
        robot.setCurrentLocation(new Coordinate(0, 0));
    
        // Act: Trigger a scan operation
        facade.scan(robot);
    
        // Assert:
        // Verify that sendRequest was called once.
        verify(mockConnection, times(1)).sendRequest(anyString());
    
        // Get the local map and verify it contains cells at (1,1) and (2,2)
        Map<Coordinate, Cell> localMap = robot.getLocalMap().getMap();
        // Use getCell() method for more direct assertions.
        Cell cell11 = robot.getLocalMap().getCell(new Coordinate(1, 1));
        Cell cell22 = robot.getLocalMap().getCell(new Coordinate(2, 2));
    
        // Assert that the cell at (1,1) is not the default cell (i.e., its type should not be UNKNOWN)
        assertNotEquals(CellType.UNKNOWN, cell11.getType(), "Cell at (1,1) should have a known type.");
        // Optionally check specific properties if needed:
        assertEquals(50, cell11.getData().getResourceUnits(), "Cell at (1,1) should have 50 resource units.");
    
        // Similarly, assert for cell (2,2)
        assertNotEquals(CellType.UNKNOWN, cell22.getType(), "Cell at (2,2) should have a known type.");
        assertEquals(100, cell22.getData().getResourceUnits(), "Cell at (2,2) should have 100 resource units.");
    }

    /**
     * Tests that a harvest request is built, sent, and the response is processed
     * correctly.
     */
    @Test
    void testHarvest() {
        // Arrange
        PlanetServerConnection mockConnection = Mockito.mock(PlanetServerConnection.class);
        String harvestResponseJson = "{\"status\":\"success\",\"action\":\"harvest\",\"message\":\"Harvest complete\","
                + "\"affectedRobots\":[{\"id\":\"r123\",\"type\":\"harvester\",\"injury\":0}],"
                + "\"detectedCells\":[]}";
        when(mockConnection.sendRequest(anyString())).thenReturn(harvestResponseJson);

        // Create the facade with the mock connection and base at (0,0)
        RobotEnvironmentFacade facade = new RobotEnvironmentFacade(mockConnection, 0, 0);

        // Create a Harvester robot at (0,0)
        Harvester robot = new Harvester(facade);
        robot.setCurrentLocation(new Coordinate(0, 0));

        // Act: Trigger a harvest operation (e.g., harvest 10 units)
        facade.harvest(robot, 10);

        // Assert: Verify that sendRequest was called once
        verify(mockConnection, times(1)).sendRequest(anyString());

        // In this test, we simply assert that no exceptions occur and the method completes.
        // Further assertions could check that the robot's state (e.g., battery or inventory)
        // has been updated accordingly.
        assertNotNull(robot, "Robot should not be null after harvest operation.");
    }
}
