/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

/**
 * JCommander validator for JSON configuration files.
 * <p>
 * Validates that a provided file path exists and points to a regular file.
 * </p>
 */
public class JsonFileValidator implements IParameterValidator {

    /**
     * Validates the specified parameter value.
     *
     * @param name  the name of the parameter
     * @param value the value of the parameter (expected to be a file path)
     * @throws ParameterException if the file does not exist or is not a regular file
     */
    @Override
    public void validate(String name, String value) throws ParameterException {
        Path path = Paths.get(value);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new ParameterException("File " + value + " does not exist.");
        }
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new ParameterException("File " + value + " is not a regular file.");
        }
    }
}