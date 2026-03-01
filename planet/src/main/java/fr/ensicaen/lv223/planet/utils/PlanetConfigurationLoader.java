/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;

/**
 * Provides utility functions for loading and parsing a planet's grid
 * configuration from a JSON in the lv223 simulation project. 
 * This class facilitates the setup of the simulation environment using
 * predefined or user-defined configurations.
 * <p>
 * It includes methods for reading and parsing JSON files, initializing
 * and populating the simulation grid, and determining grid dimensions.
 * 
 * <p>Note: The JSON file format is based on the JSON 2.0 format, which is
 * supported by the Jackson library. The JSON 1.0 format is also supported
 * by the Jackson library, but has been deprecated in this class.
 * 
 * @version 2.0
 * @since 1.0 (lv223-2024 simulation project)
 */
public class PlanetConfigurationLoader {

    private PlanetConfigurationLoader() {
    }

    /**
     * Loads the initial configuration of the planet's grid from a JSON version 2.0 file.
     * <p>
     * Example of a JSON 2.0 file:
     * <pre>
     * {
     *    "metadata": {
     *       "version": "2.0",
     *       "date": "2023-08-26",
     *       "gridSize": {
     *          "width": 5, 
     *          "height": 2
     *       }
     *    },
     *    "cells": [
     *       {
     *          "x": 0,
     *          "y": 0, 
     *          "type": "WATER",
     *          "quantity": 100,
     *          "visited": "false",
     *          "modified": "false",
     *          "has_pipeline": "false"
     *       },
     *       ...
     *    ]
     * }
     * </pre>
     * 
     * @param filePath the path to the JSON 2.0 file containing the grid configuration
     * @return the grid of cells based on the configuration
     * @throws IOException if there is an error reading the file or parsing JSON
     */
    public static Cell[][] loadConfiguration(String filePath) throws IOException {
        String jsonText = readJsonFile(filePath);
        JsonNode rootNode = parseJson(jsonText);

        // Read metadata
        JsonNode metadataNode = rootNode.get("metadata");
        int width = metadataNode.get("gridSize").get("width").asInt();
        int height = metadataNode.get("gridSize").get("height").asInt();

        // Initialize grid based on metadata
        Cell[][] grid = new Cell[height][width];
        for (Cell[] row : grid) {
            Arrays.fill(row, new Cell(CellType.UNKNOWN, 0)); // Assuming a constructor exists for UNKNOWN type
        }

        // Populate the grid with cells
        JsonNode cellsNode = rootNode.get("cells");
        for (JsonNode cellNode : cellsNode) {
            int x = cellNode.get("x").asInt();
            int y = cellNode.get("y").asInt();
            CellType type = CellType.valueOf(cellNode.get("type").asText().toUpperCase());
            int units = cellNode.get("quantity").asInt();
            boolean visited = cellNode.get("visited").asBoolean();
            boolean modified = cellNode.get("modified").asBoolean();
            boolean hasPipeline = cellNode.get("has_pipeline").asBoolean();

            Cell cell = new Cell(type, units);
            cell.setVisited(visited);
            cell.setModified(modified);
            cell.setHasAlienConstructionOnIt(hasPipeline);
            grid[y][x] = cell;
        }

        return grid;
    }

    /**
     * Reads the contents of a JSON file into a string.
     *
     * @param filePath the path to the file
     * @return the contents of the file as a string
     * @throws IOException if there is an error reading the file
     */
    private static String readJsonFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    /**
     * Parses the given JSON text and returns a JsonNode object representing
     * the parsed JSON.
     *
     * @param jsonText the JSON text to parse
     * @return a JsonNode object representing the parsed JSON
     * @throws IOException if an I/O error occurs while parsing the JSON
     */
    private static JsonNode parseJson(String jsonText) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonText);
    }

    /**
     * Loads the initial configuration of the planet's grid from a JSON version 1.O file.
     * <p>
     * Example of a JSON 1.0 file:
     * <pre>
     * [
     *   {   
     *      "type": "WATER",
     *      "cellPos": [
     *         {
     *            "y": 0,
     *            "x": 0,
     *            "quantity": 100
     *          },
     *          {
     *             ...
     *          }   
     *       ]
     *   },
     *   {
     *      "type": "STONE",
     *      "cellPos": [
     *         {
     *            ...
     *         },
     *         ...
     *      ]
     *   }
     * ]
     * </pre>
     *
     * @param filePath the path to the JSON version 1.0 file containing the grid configuration
     * @return the grid of cells based on the configuration
     * @throws IOException if there is an error reading the file or parsing JSON
     * @deprecated Use PlanetConfigurationLoader.loadConfiguration instead
     */
    public static Cell[][] loadConfigurationVersion1(String filePath) throws IOException {
        String jsonText = readJsonFile(filePath);
        JsonNode rootNode = parseJson(jsonText);

        Dimension gridDimension = getMaxGridDimensions(rootNode);
        Cell[][] grid = initializeGrid(gridDimension);

        populateGridWithCells(rootNode, grid);
        return grid;
    }

    /**
     * Determines the maximum dimensions of the grid based on the given JSON node.
     * @param rootNode the JSON node containing the cell positions
     * @return the maximum dimensions of the grid
     * @deprecated Used with PlanetConfigurationLoader.loadConfigurationVersion1
     */
    private static Dimension getMaxGridDimensions(JsonNode rootNode) {
        int maxX = 0;
        int maxY = 0;

        for (JsonNode node : rootNode) {
            JsonNode cellPosNode = node.get("cellPos");
            for (JsonNode posNode : cellPosNode) {
                int x = posNode.get("x").asInt();
                int y = posNode.get("y").asInt();
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        return new Dimension(maxX + 1, maxY + 1);
    }

    /**
     * Initializes a grid of cells with the given dimensions.
     * @param dimension the dimensions of the grid
     * @return the grid of cells
     * @deprecated Used with PlanetConfigurationLoader.loadConfigurationVersion1
     */
    private static Cell[][] initializeGrid(Dimension dimension) {
        Cell[][] grid = new Cell[dimension.height][dimension.width];
        for (Cell[] cells : grid) {
            Arrays.fill(cells, new Cell(CellType.UNKNOWN, 0));
        }
        return grid;
    }

    /**
     * Populates the given grid with cells based on the given JSON node.
     * @param rootNode  the JSON node containing the cell positions and types
     * @param grid  the cell grid to be populated
     * @deprecated Used with PlanetConfigurationLoader.loadConfigurationVersion1
     */
    private static void populateGridWithCells(JsonNode rootNode, Cell[][] grid) {
        for (JsonNode node : rootNode) {
            CellType type = CellType.valueOf(node.get("type").asText());
            JsonNode cellPosNode = node.get("cellPos");
            for (JsonNode posNode : cellPosNode) {
                int x = posNode.get("x").asInt();
                int y = posNode.get("y").asInt();
                int quantity = posNode.get("quantity").asInt();
                grid[y][x] = new Cell(type, quantity);
            }
        }
    }

    /**
     * Represents the dimensions of a grid.
     */
    private static class Dimension {
        public final int width;
        public final int height;

        public Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
