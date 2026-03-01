/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.season;

/**
 * Manages the progression of seasons on the planet.
 * 
 * <p>
 * This class tracks the current season and facilitates transitions at regular intervals.
 * It also provides a method to check whether a season change is due based on the simulation turn.
 * </p>
 *
 * @since 1.0 (lv223 simulation project)
 */
public class SeasonHandler {
    
    /** The current season of the planet. */
    private Season currentSeason;
    
    /** The duration of each season (in simulation turns). */
    private final int seasonDuration;

    /**
     * Constructs a SeasonHandler with the specified season duration.
     *
     * @param seasonDuration the duration (in simulation turns) of each season
     */
    public SeasonHandler(int seasonDuration) {
        this.currentSeason = Season.SUMMER; // Default starting season
        this.seasonDuration = seasonDuration;
    }

    /**
     * Returns the current active season of the planet.
     *
     * @return the current season
     */
    public Season getCurrentSeason() {
        return currentSeason;
    }

    /**
     * Advances to the next season.
     */
    public void changeSeason() {
        Season[] seasons = Season.values();
        int nextIndex = (currentSeason.ordinal() + 1) % seasons.length;
        currentSeason = seasons[nextIndex];
    }

    /**
     * Determines whether the season should change at the specified simulation turn.
     *
     * @param currentTurn the current simulation turn
     * @return {@code true} if the season should change; {@code false} otherwise
     */
    public boolean shouldChangeSeason(int currentTurn) {
        // A season change occurs at turn 0 or when the current turn is a multiple of the season duration.
        return currentTurn == 0 || (currentTurn % seasonDuration) == 0;
    }
}
