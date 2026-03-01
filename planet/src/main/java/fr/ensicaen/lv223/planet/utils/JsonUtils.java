/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON operations.
 * <p>
 * Provides a single shared {@code ObjectMapper} instance configured for
 * use across the application. This centralizes JSON configurations and
 * ensures consistent behavior for all JSON operations.
 * </p>
 */
public final class JsonUtils {

    /** The shared ObjectMapper instance */
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * Private constructor to prevent instantiation.
     */
    private JsonUtils() {
        // Prevent instantiation
    }

    /**
     * Creates and configures an {@code ObjectMapper} instance.
     * <p>
     * The configuration ignores unknown properties during deserialization.
     * Additional configurations can be added as needed.
     * </p>
     *
     * @return a configured ObjectMapper instance.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Returns the shared {@code ObjectMapper} instance.
     *
     * @return the shared ObjectMapper.
     */
    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
}
