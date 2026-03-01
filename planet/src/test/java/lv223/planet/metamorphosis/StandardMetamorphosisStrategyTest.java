/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package lv223.planet.metamorphosis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.metamorphosis.StandardMetamorphosisStrategy;
import fr.ensicaen.lv223.planet.season.Season;
import fr.ensicaen.lv223.planet.utils.Config;

class StandardMetamorphosisStrategyTest {
    private static final String CONFIG_FILE_PATH = "target/test-classes/json/planet2.json";

    private Config config;
    private Planet planet;
    private StandardMetamorphosisStrategy strategy;

    @BeforeEach
    void setUp() {
        // Initialize planet and strategy before each test
        config = new Config();
        planet = new Planet(config);
        strategy = new StandardMetamorphosisStrategy();
    }

    @Test
    void applySeasonalChangesTest() {
        double probability = 0.0; // Example probability
        planet.setChangeStrategy(strategy);
        Season season = planet.getSeasonHandler().getCurrentSeason(); 
        strategy.applySeasonalChanges(planet, season);

        assertFalse(planet.hasSignificantChanges());
    }

    @Test
    void applyExtractionChangesTest() {
        int probability = 5; // Example probability
        planet.setChangeStrategy(strategy);

        strategy.applyExtractionChanges(planet, probability);

        assertFalse(planet.hasSignificantChanges());
    }

}
