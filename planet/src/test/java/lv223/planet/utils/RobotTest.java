/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet.utils;

import org.junit.jupiter.api.Test;

import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {
    RobotType[] types = { RobotType.CARTOGRAPHER, RobotType.FARMER, RobotType.MINER, RobotType.HARVESTER, RobotType.PIPELINER };

    @Test
    void testGetType() {
        RobotInfo robot = new RobotInfo("C1", types[0], 10, 20);
        assertEquals(RobotType.CARTOGRAPHER, robot.getType());
    }

    @Test
    void testGetTypeAsFormattedString() {
        int i = 0;
        String[] typesStr = { "Cartographer", "Farmer", "Miner", "Harvester", "Pipeliner" };

        for (RobotType type : types) {
            RobotInfo robot = new RobotInfo(typesStr[i].substring(0,1), type, 10, 20);
            assertEquals(typesStr[i], robot.getType().toFormattedString());
            i++;
        }
    }

    @Test
    void testGetX() {
        RobotInfo robot = new RobotInfo("F1", types[1], 10, 20);
        assertEquals(10, robot.getX());
    }

    @Test
    void testGetY() {
        RobotInfo robot = new RobotInfo("F1", types[1], 10, 20);
        assertEquals(20, robot.getY());
    }

    @Test
    void testSetType() {
        RobotInfo robot = new RobotInfo("C1", types[0], 10, 20);
        robot.setType(types[1]);
        assertEquals(RobotType.FARMER, robot.getType());
    }

    @Test
    void testSetX() {
        RobotInfo robot = new RobotInfo("C1", types[0], 10, 20);
        robot.setX(15);
        assertEquals(15, robot.getX());
    }

    @Test
    void testSetY() {
        RobotInfo robot = new RobotInfo("C1", types[0], 10, 20);
        robot.setY(15);
        assertEquals(15, robot.getY());
    }

}
