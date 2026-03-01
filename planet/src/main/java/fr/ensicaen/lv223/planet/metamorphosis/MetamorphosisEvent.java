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

/**
 * Represents a significant metamorphosis event on the planet.
 * <p>
 * This class encapsulates details such as the timestamp and description of events
 * that trigger changes in the planet's state or environment. It is used to log these
 * occurrences and their impacts.
 * </p>
 *
 * @version 1.1
 * @since 1.0
 */
public class MetamorphosisEvent {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(MetamorphosisEvent.class);

    /** The timestamp of when the event occurred. */
    private long timestamp; 

    /** A description of the metamorphosis event. */
    private String description;

    /**
     * Constructs a new MetamorphosisEvent with the specified description.
     * The timestamp is set to the current system time.
     *
     * @param description the description of the metamorphosis event
     */
    public MetamorphosisEvent(String description) {
        this.timestamp = System.currentTimeMillis();
        this.description = description;
        logger.debug("Metamorphosis event created: {}", description);
    }

    /**
     * Returns the timestamp when this event occurred.
     *
     * @return the event timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for this event.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the description of the event.
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
