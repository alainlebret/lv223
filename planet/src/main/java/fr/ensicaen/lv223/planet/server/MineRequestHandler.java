/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.pojo.AffectedRobot;
import fr.ensicaen.lv223.planet.pojo.DetectedCell;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Handles requests to extract minerals from cells.
 * <p>
 * This handler validates a mining request by ensuring that the target cell is suitable
 * for mineral extraction and that the requested quantity is valid. If the request is valid,
 * the handler performs the extraction, triggers metamorphosis in neighboring cells,
 * records the extraction event, and updates the colony's robot registry.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class MineRequestHandler extends AbstractRequestHandler {
    
    private static final Logger logger = LogManager.getLogger(MineRequestHandler.class);
    public static final int MAX_EXTRACTED_MINERAL_PER_CELL = 100;

    /**
     * Handles a mineral extraction request.
     *
     * @param context the request context containing the server and other info
     * @param request the action request from the client
     * @param params  the extracted parameters from the request
     * @param x       the x-coordinate of the target cell
     * @param y       the y-coordinate of the target cell
     * @return a JSON string representing the success or error response
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request,
            ActionParameters params, int x, int y) throws JsonProcessingException {
        try {
            // Retrieve the target cell from the planet grid.
            Cell cell = context.getServer().planet.getGrid()[y][x];
            if (!isSuitableForMining(cell)) {
                logger.error("Cell at ({}, {}) is not suitable for mining (robot id: {}).", 
                        x, y, request.getRobotId());
                return createErrorResponse(request, ResponseType.INVALID_CELL);
            }
    
            int requestedUnits = params.getUnits();
            if (!isValidQuantity(requestedUnits)) {
                logger.error("Invalid quantity {} requested by robot {}.", 
                        requestedUnits, request.getRobotId());
                return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
            }
    
            if (!hasEnoughUnitsToMine(cell, requestedUnits)) {
                logger.error("Not enough mineral units in cell at ({}, {}) for robot {}: available={}, requested={}",
                        x, y, request.getRobotId(), cell.getUnits(), requestedUnits);
                return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
            }
    
            // Calculate the radius that will be affected by the mining extraction.
            int extractionRadius = calculateRadius(requestedUnits);
    
            // Trigger metamorphosis in neighboring cells.
            cell.triggerNeighboringCellMetamorphosis(context.getServer().planet, x, y,
                    extractionRadius, context.getServer().planet.getMetamorphosisProbability());
    
            // Extract minerals from the cell.
            extractMineral(cell, requestedUnits);
    
            // Record the extraction event on the planet.
            context.getServer().planet.recordExtractionEvent(requestedUnits);
    
            // Update the colony's robot registry with the new position, preserving
            // any accumulated injury status from prior actions.
            RobotInfo existing = context.getServer().getColonyRobots().get(request.getRobotId());
            if (existing != null) {
                existing.setX(x);
                existing.setY(y);
            } else {
                context.getServer().getColonyRobots().put(
                        request.getRobotId(),
                        new RobotInfo(request.getRobotId(), RobotType.fromString(request.getRobotType()), x, y));
            }
    
            // Determine affected robots based on proximity to the extraction area.
            List<AffectedRobot> affectedRobots = findAffectedRobots(context.getServer(), x, y, extractionRadius);
    
            return createSuccessResponse(request, new ArrayList<DetectedCell>(), affectedRobots);
        } catch (Exception e) {
            logger.error("Error in MineRequestHandler for request {}: {}", request, e.getMessage(), e);
            return createErrorResponse(request, ResponseType.ERROR);
        }
    }

    /**
     * Validates that the robot type is MINER.
     *
     * @param type the robot type
     * @return {@code true} if the type is MINER; {@code false} otherwise
     */
    @Override
    public boolean isValidRobotType(RobotType type) {
        return type == RobotType.MINER;
    }

    /**
     * Determines if the specified cell is suitable for mining.
     *
     * @param cell the cell to check
     * @return {@code true} if the cell is of type MINERAL and has resources; {@code false} otherwise
     */
    private boolean isSuitableForMining(Cell cell) {
        return cell.getType() == CellType.MINERAL && cell.getUnits() > 0;
    }

    /**
     * Validates the requested extraction quantity.
     *
     * @param units the requested quantity
     * @return {@code true} if the quantity is within allowed limits; {@code false} otherwise
     */
    private boolean isValidQuantity(int units) {
        return units >= 0 && units <= MAX_EXTRACTED_MINERAL_PER_CELL;
    }

    /**
     * Checks if the cell has sufficient units for the requested extraction.
     *
     * @param cell  the cell from which minerals are to be extracted
     * @param units the requested extraction quantity
     * @return {@code true} if the cell has at least the requested number of units; {@code false} otherwise
     */
    private boolean hasEnoughUnitsToMine(Cell cell, int units) {
        return cell.getUnits() >= units;
    }

    /**
     * Extracts the specified number of mineral units from the cell.
     *
     * @param cell  the cell from which to extract minerals
     * @param units the quantity of units to extract
     */
    private void extractMineral(Cell cell, int units) {
        cell.setUnits(cell.getUnits() - units);
        cell.setVisited(true);
    }

    /**
     * Calculates the extraction radius based on the requested units.
     *
     * @param units the number of units to extract
     * @return the radius (in cells) affected by the extraction
     */
    private int calculateRadius(int units) {
        return 1 + (units / MAX_EXTRACTED_MINERAL_PER_CELL);
    }

    /**
     * Identifies and returns a list of robots affected by the extraction event.
     *
     * @param server   the planet server instance
     * @param actionX  the x-coordinate of the extraction event
     * @param actionY  the y-coordinate of the extraction event
     * @param radius   the extraction radius
     * @return a list of {@code AffectedRobot} instances representing affected robots
     */
    private List<AffectedRobot> findAffectedRobots(PlanetServer server, int actionX, int actionY, int radius) {
        List<AffectedRobot> affected = new ArrayList<>();
        server.getColonyRobots().forEach((id, robotInfo) -> {
            if (isWithinRadius(robotInfo.getX(), robotInfo.getY(), actionX, actionY, radius)) {
                // Robots on STONE, MINERAL or BASE cells are shielded from blast injuries;
                // Cartographers are always immune (battery-only failure mode).
                Cell cell = server.planet.getGrid()[robotInfo.getY()][robotInfo.getX()];
                int injury = (cell.getType() == CellType.STONE || cell.getType() == CellType.MINERAL
                        || cell.getType() == CellType.BASE
                        || robotInfo.getType() == RobotType.CARTOGRAPHER) ? 0 : calculateInjury(radius);
                affected.add(new AffectedRobot(id, robotInfo.getType().toString(), injury));
            }
        });
        return affected;
    }

    /**
     * Checks if a robot is within the specified radius of an action point.
     *
     * @param robotX  the robot's x-coordinate
     * @param robotY  the robot's y-coordinate
     * @param actionX the x-coordinate of the extraction event
     * @param actionY the y-coordinate of the extraction event
     * @param radius  the extraction radius
     * @return {@code true} if the robot is within the radius; {@code false} otherwise
     */
    private boolean isWithinRadius(int robotX, int robotY, int actionX, int actionY, int radius) {
        int deltaX = actionX - robotX;
        int deltaY = actionY - robotY;
        int distanceSquared = deltaX * deltaX + deltaY * deltaY;
        return distanceSquared <= radius * radius;
    }

    /**
     * Calculates the injury incurred from the extraction based on the extraction radius.
     *
     * @param radius the extraction radius
     * @return an injury value computed as baseInjury minus twice the radius, but not less than 0
     */
    private int calculateInjury(int radius) {
        int baseInjury = 10;
        return Math.max(0, baseInjury - radius * 2);
    }
}
