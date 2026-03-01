/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Handles requests to construct pipelines between water terrains and the base.
 * <p>
 * This handler validates the cell suitability for pipeline construction.
 * If the cell is suitable, it registers the robot's current position and
 * triggers the pipeline construction process.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class PipeRequestHandler extends AbstractRequestHandler {

    /**
     * Processes a pipeline construction request.
     *
     * @param context the associated request context.
     * @param request the action request from the client.
     * @param params  the action request parameters.
     * @param x       the x-coordinate of the cell.
     * @param y       the y-coordinate of the cell.
     * @return a JSON string representing either a success or error response.
     * @throws JsonProcessingException if an error occurs during JSON processing.
     */
    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request, ActionParameters params,
            int x, int y) throws JsonProcessingException {

        // Retrieve the target cell from the planet grid.
        Cell cell = context.getServer().planet.getGrid()[y][x];

        // Validate that the cell is suitable for pipeline construction.
        if (isNotSuitableForPipeline(cell)) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

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

        // Construct the pipeline at the target cell.
        constructPipeline(cell);

        // Return a success response.
        return createSuccessResponse(request, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Validates that the given robot type is valid for pipeline construction.
     *
     * @param type the robot type to check.
     * @return {@code true} if the robot type is PIPELINER; {@code false} otherwise.
     */
    @Override
    public boolean isValidRobotType(RobotType type) {
        return type == RobotType.PIPELINER;
    }

    /**
     * Checks if the cell is not suitable for constructing a pipeline.
     *
     * @param cell the cell to check.
     * @return {@code true} if the cell is unsuitable; {@code false} otherwise.
     */
    private boolean isNotSuitableForPipeline(Cell cell) {
        // A cell is unsuitable if it is IMPENETRABLE, WATER, or UNKNOWN.
        return cell.getType() == CellType.IMPENETRABLE 
                || cell.getType() == CellType.WATER 
                || cell.getType() == CellType.UNKNOWN;
    }

    /**
     * Constructs a pipeline segment in the given cell.
     *
     * @param cell the cell in which the pipeline segment is constructed.
     */
    private void constructPipeline(Cell cell) {
        cell.setHasAlienConstructionOnIt(true);
        cell.setVisited(true);
    }
}
