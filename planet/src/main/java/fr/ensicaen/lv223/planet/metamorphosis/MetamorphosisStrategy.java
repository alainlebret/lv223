/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.metamorphosis;

import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.season.Season;

/**
 * Defines metamorphosis strategies for transforming the planet.
 * <p>
 * Implementations of this interface provide the logic for altering the
 * planet's state based on seasonal changes, resource extraction intensity,
 * and other influencing factors.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface MetamorphosisStrategy {

    /**
     * Applies metamorphosis changes to the specified planet based on the current season.
     *
     * @param planet the planet to transform; must not be {@code null}
     * @param season the current season; must not be {@code null}
     */
    void applySeasonalChanges(Planet planet, Season season);

    /**
     * Applies metamorphosis changes to the specified planet based on resource extraction intensity.
     *
     * @param planet              the planet to transform; must not be {@code null}
     * @param extractionIntensity the intensity of resource extraction
     */
    void applyExtractionChanges(Planet planet, double extractionIntensity);
}
