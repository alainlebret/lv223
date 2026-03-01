/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet;

import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.season.Season;
import fr.ensicaen.lv223.planet.season.SeasonHandler;
import fr.ensicaen.lv223.planet.utils.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlanetTest {

    private Config config;
    private Planet planet;

    @BeforeEach
    void setUp() {
        config = new Config();
        planet = new Planet(config);
    }

    @Test
    void testGetGrid() {
        assertNotNull(planet.getGrid());
    }

    @Test
    void testGetCurrentSeason() {
        // Assuming the initial season is SUMMER as per the constructor
        assertEquals(Season.SUMMER, planet.getSeasonHandler().getCurrentSeason(), "Initial season should be the last day of SUMMER.");
    }

    @Test
    void testGetYearCounter() {
        // Assuming the initial year counter is 0
        assertEquals(0, planet.getCurrentYear(), "Initial year counter should be 0.");
    }

    @Test
    void testGetCurrentTurn() {
        // Assuming the initial turn is 0
        assertEquals(0, planet.getCurrentTurn(), "Initial turn should be 0.");
    }

    @Test
    void testGetWidth() {
        assert planet != null;
        assertEquals(21, planet.getWidth(), "Width of sample planet should be 21.");
    }

    @Test
    void testGetHeight() {
        assert planet != null;
        assertEquals(21, planet.getHeight(), "Width of sample planet should be 21.");
    }

    @Test
    void testNextTurn() {
        planet.nextTurn();
        assertEquals(1, planet.getCurrentTurn(), "After calling nextTurn, turns should be 1.");
        planet.nextTurn();
        planet.nextTurn();
        planet.nextTurn();
        assertEquals(4, planet.getCurrentTurn(), "After calling nextTurn, turns should be 4.");
    }

    @Test
    void testChangeSeason() {
        SeasonHandler seasonHandler = planet.getSeasonHandler();
        assertEquals(Season.SUMMER, seasonHandler.getCurrentSeason(), "Should be SUMMER after one change.");

        seasonHandler.changeSeason();
        assertEquals(Season.AUTUMN, seasonHandler.getCurrentSeason(), "Should be AUTUMN after one change.");

        seasonHandler.changeSeason();
        assertEquals(Season.WINTER, seasonHandler.getCurrentSeason(), "Should be WINTER after two changes.");

        seasonHandler.changeSeason();
        assertEquals(Season.SPRING, seasonHandler.getCurrentSeason(), "Should be SPRING after three changes.");

        seasonHandler.changeSeason();
        assertEquals(Season.SUMMER, seasonHandler.getCurrentSeason(), "Should cycle back to SUMMER after four changes.");
    }

    @Test
    void testApplySeasonalChanges() {
        // Clear existing metamorphosis events
        planet.clearMetamorphosisMemory();
    
        // Simulate enough turns to trigger the method
        for (int i = 0; i < Planet.SEASON_DURATION * 2; i++) {
            planet.nextTurn();
        }
    
        // Check if metamorphosis events have been recorded
        assertTrue(planet.getMetamorphosisCount() > 0, "Metamorphosis events should be recorded after seasonal changes.");
    }

}
