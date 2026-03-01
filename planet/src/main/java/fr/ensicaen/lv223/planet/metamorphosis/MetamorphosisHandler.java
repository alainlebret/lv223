/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.metamorphosis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.exception.MetamorphosisException;
import fr.ensicaen.lv223.planet.season.Season;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 * Manages metamorphosis processes on the planet.
 * <p>
 * This class applies transformations based on extraction events, seasonal
 * conditions, and health state using an encapsulated metamorphosis strategy.
 * It also integrates a fuzzy logic system to determine the metamorphosis intensity.
 * </p>
 *
 * @version 1.3
 * @since 1.0
 */
public class MetamorphosisHandler {
    private static final Logger logger = LogManager.getLogger(MetamorphosisHandler.class);

    /** The current metamorphosis strategy. */
    private MetamorphosisStrategy strategy;

    /** The current metamorphosis probability. */
    private double metamorphosisProbability;

    /** The fuzzy logic system used for determining metamorphosis level. */
    private final FIS fuzzyLogicSystem;

    /**
     * Constructs a new {@code MetamorphosisHandler} with the given strategy and initial probability.
     * <p>
     * The fuzzy logic system (FCL file) is loaded from the classpath.
     * </p>
     *
     * @param strategy           the metamorphosis strategy to use
     * @param initialProbability the initial metamorphosis probability
     * @throws MetamorphosisException if the fuzzy logic file cannot be loaded
     */
    public MetamorphosisHandler(MetamorphosisStrategy strategy, double initialProbability)
            throws MetamorphosisException {
        this.strategy = strategy;
        this.metamorphosisProbability = initialProbability;

        String fclFilePath;
        try {
            // Load the FCL file from the classpath (ensure it exists under resources)
            fclFilePath = getClass().getResource("/metamorphosis.fcl").getPath();
        } catch (Exception e) {
            throw new MetamorphosisException("FCL file not found in the classpath");
        }

        fuzzyLogicSystem = FIS.load(fclFilePath, true);
        if (fuzzyLogicSystem == null) {
            throw new MetamorphosisException("Impossible to load FCL file: " + fclFilePath);
        }
        logger.debug("FCL file loaded from: {}", fclFilePath);
    }

    /**
     * Applies seasonal metamorphosis changes to the specified planet.
     *
     * @param planet the planet to transform; must not be {@code null}
     * @param season the current season
     */
    public void applySeasonalChanges(Planet planet, Season season) {
        strategy.applySeasonalChanges(planet, season);
    }

    /**
     * Applies metamorphosis changes to the planet based on extraction activities.
     *
     * @param planet            the planet to transform; must not be {@code null}
     * @param mineralExtraction the mineral extraction amount
     * @param waterExtraction   the water extraction amount
     */
    public void applyExtractionChanges(Planet planet, double mineralExtraction, double waterExtraction) {
        double planetHealth = planet.getHealthStateHandler().getCurrentHealthNumericalValue();
        double extractionIntensity = calculateExtractionMetamorphosisLevel(mineralExtraction, waterExtraction, planetHealth);
        strategy.applyExtractionChanges(planet, extractionIntensity);
    }

    /**
     * Calculates the metamorphosis level based on extraction activity and current planet health.
     *
     * @param mineralExtraction the mineral extraction amount
     * @param waterPumping      the water pumping amount
     * @param planetHealth      the current numerical health of the planet
     * @return the computed metamorphosis level
     */
    private double calculateExtractionMetamorphosisLevel(double mineralExtraction, double waterPumping,
                                                         double planetHealth) {
        fuzzyLogicSystem.setVariable("mineralExtraction", mineralExtraction);
        fuzzyLogicSystem.setVariable("waterPumping", waterPumping);
        fuzzyLogicSystem.setVariable("planetHealth", planetHealth);
        fuzzyLogicSystem.evaluate();
        return fuzzyLogicSystem.getVariable("metamorphosisLevel").getValue();
    }

    /**
     * Sets a new metamorphosis strategy.
     *
     * @param newStrategy the new strategy to use; must not be {@code null}
     */
    public void setStrategy(MetamorphosisStrategy newStrategy) {
        if (newStrategy == null) {
            throw new IllegalArgumentException("newStrategy cannot be null");
        }
        this.strategy = newStrategy;
    }

    /**
     * Sets the metamorphosis probability.
     *
     * @param probability the new metamorphosis probability
     */
    public void setMetamorphosisProbability(double probability) {
        this.metamorphosisProbability = probability;
    }

    /**
     * Returns the current metamorphosis probability.
     *
     * @return the metamorphosis probability
     */
    public double getMetamorphosisProbability() {
        return metamorphosisProbability;
    }
}
