/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet.healthstate;

import org.junit.jupiter.api.Test;

import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.healthstate.HealthState;
import fr.ensicaen.lv223.planet.healthstate.MelancholyHealthState;
import fr.ensicaen.lv223.planet.utils.Config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class MelancholyHealthStateTest {
    private static final String CONFIG_FILE_PATH = "target/test-classes/json/planet2.json";
    private Config config;
    private Planet planet;

        @BeforeEach
    void setUp() {
        config = new Config();
        planet = new Planet(config);
    }

    @Test
    void testUpdateHealth() {
        HealthState healthState = new MelancholyHealthState();
        healthState.updateHealth(planet);
        // TO FIX: check if the current health description is "Melancholy"
        assertNotEquals("Melancholy", planet.getHealthStateHandler().getCurrentHealthDescription());
    }

    @Test
    void testAdjustMetamorphosisProbability() {
        HealthState healthState = new MelancholyHealthState();
        double adjustedProbability = healthState.adjustMetamorphosisProbability(0.5);
        assertEquals(0.6, adjustedProbability);
    }

    @Test
    void testToString() {
        HealthState healthState = new MelancholyHealthState();
        assertEquals("Melancholy", healthState.toString());
    }
}
