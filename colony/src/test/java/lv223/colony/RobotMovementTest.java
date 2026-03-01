/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.colony;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.ensicaen.lv223.colony.communication.PlanetServerConnection;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.robot.Cartographer;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.utils.Direction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RobotMovementTest {

    @Test
    void testRobotMovement() {
        // Création du mock pour PlanetServerConnection
    PlanetServerConnection mockConnection = Mockito.mock(PlanetServerConnection.class);

    // Réponse JSON pour l'action "move"
    String moveResponse = "{\"status\":\"success\",\"action\":\"move\",\"message\":\"\",\"affectedRobots\":[{\"id\":\"C1\",\"type\":\"Cartographer\",\"injury\":0}],\"detectedCells\":[]}";
    // Réponse JSON pour l'action "scan"
    String scanResponse = "{\"status\":\"success\",\"action\":\"scan\",\"message\":\"Scan completed\",\"affectedRobots\":[],\"detectedCells\":[]}";

    // Configuration du mock en fonction du contenu de la requête
    when(mockConnection.sendRequest(argThat(s -> s != null && s.contains("\"action\":\"move\"")))).thenReturn(moveResponse);
    when(mockConnection.sendRequest(argThat(s -> s != null && s.contains("\"action\":\"scan\"")))).thenReturn(scanResponse);
    // Création de la façade avec le mock
    RobotEnvironmentFacade facade = new RobotEnvironmentFacade(mockConnection, 0, 0); // Base à (0,0)

    // Création d'un robot (par exemple, un Cartographer) et initialisation de sa position locale à (0,0)
    Robot robot = new Cartographer(facade);
    robot.setCurrentLocation(new Coordinate(0, 0));

    // Appel à la méthode navigate qui doit appeler move et scan
//    robot.performTask();

    // Vérification que sendRequest a été appelé deux fois
//    Mockito.verify(mockConnection, times(2)).sendRequest(anyString());

    // Vérification de la mise à jour de la position (pour un déplacement SE, la nouvelle position locale attendue est (1,1))
//    Coordinate expectedLocation = new Coordinate(1, 1);
//    assertEquals(expectedLocation, robot.getCurrentLocation(), "Robot did not move to the expected location.");
}

    private String createMoveRequest(String id, String type, int x, int y, int newX, int newY) {
        return "{ \"action\": \"move\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + ", \"newX\": " + newX + ", \"newY\": " + newY
                + " } }";
    }

}
