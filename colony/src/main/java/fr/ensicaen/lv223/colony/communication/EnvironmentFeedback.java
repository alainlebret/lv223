/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.ensicaen.lv223.colony.utils.CellData;
import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.robot.Robot;

/**
 * Represents feedback about the environment or robot status within the simulation.
 * <p>
 * This class is used to convey different types of information—such as scan results or
 * reports of injured robots—from the environment to robots or other interested parties.
 * Only one type of feedback is applicable at a time.
 * </p>
 */
public class EnvironmentFeedback {
    private final FeedbackType type;
    private final Map<Coordinate, CellData> scanResults;
    private final List<Robot> injuredRobots;

    /**
     * Constructs an EnvironmentFeedback instance for scan results.
     *
     * @param type        the type of feedback
     * @param scanResults a map containing scan results keyed by cell coordinates
     */
    public EnvironmentFeedback(FeedbackType type, Map<Coordinate, CellData> scanResults) {
        this.type = type;
        this.scanResults = scanResults;
        this.injuredRobots = null;
    }

    /**
     * Constructs an EnvironmentFeedback instance for injured robots.
     *
     * @param type           the type of feedback
     * @param injuredRobots  a list of injured robots
     */
    public EnvironmentFeedback(FeedbackType type, List<Robot> injuredRobots) {
        this.type = type;
        this.injuredRobots = injuredRobots;
        this.scanResults = null;
    }

    /**
     * Returns the type of feedback.
     *
     * @return the feedback type.
     */
    public FeedbackType getType() {
        return type;
    }

    /**
     * Returns the scan results.
     *
     * @return a map of coordinates to cell data representing the scan results, or null if not applicable.
     */
    public Map<Coordinate, CellData> getScanResults() {
        return scanResults == null ? null : Collections.unmodifiableMap(scanResults);
    }

    /**
     * Returns the list of injured robots.
     *
     * @return an unmodifiable list of injured robots, or null if not applicable.
     */
    public List<Robot> getInjuredRobots() {
        return injuredRobots == null ? null : Collections.unmodifiableList(injuredRobots);
    }

    /**
     * Creates an EnvironmentFeedback instance representing scan results.
     *
     * @param scanResults a map of coordinates to cell data
     * @return an EnvironmentFeedback instance with type SCAN_RESULT
     */
    public static EnvironmentFeedback scanResult(Map<Coordinate, CellData> scanResults) {
        return new EnvironmentFeedback(FeedbackType.SCAN_RESULT, scanResults);
    }

    /**
     * Creates an EnvironmentFeedback instance representing injured robots.
     *
     * @param injuredRobots a list of injured robots
     * @return an EnvironmentFeedback instance with type INJURED
     */
    public static EnvironmentFeedback injuredRobots(List<Robot> injuredRobots) {
        return new EnvironmentFeedback(FeedbackType.INJURED, injuredRobots);
    }
}
