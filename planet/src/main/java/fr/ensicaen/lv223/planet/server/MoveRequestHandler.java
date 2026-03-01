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

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.pojo.AffectedRobot;
import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Handles requests to move robots to specific coordinates.
 * <p>
 * This handler validates the destination cell for a move request. It checks
 * that the destination is within valid boundaries and is appropriate for the
 * moving robot’s type. Upon a successful move request, it updates the colony’s
 * robot registry and returns a success response.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class MoveRequestHandler extends AbstractRequestHandler {

    /**
     * Handles a move request for a robot.
     *
     * @param context the request context containing server information
     * @param request the action request from the client
     * @param params  the action parameters extracted from the request
     * @param currentX the current x-coordinate of the robot's cell
     * @param currentY the current y-coordinate of the robot's cell
     * @return a JSON string representing either a success or error response
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request,
                                             ActionParameters params, int currentX, int currentY)
            throws JsonProcessingException {

        // Retrieve the current cell of the robot.
        Cell currentCell = context.getServer().planet.getGrid()[currentY][currentX];

        // Extract the destination coordinates from the request parameters.
        int destinationX = params.getNewX();
        int destinationY = params.getNewY();

        // Validate the destination cell.
        if (context.getServer().isInvalidCell(destinationX, destinationY)) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        Cell destinationCell = context.getServer().planet.getGrid()[destinationY][destinationX];
        RobotType robotType = RobotType.fromString(request.getRobotType());
        if (!isValidDestination(destinationCell, robotType)) {
            return createErrorResponse(request, ResponseType.INVALID_DESTINATION_CELL);
        }

        // Mark the current cell as visited.
        currentCell.setVisited(true);

        // Update the colony's robot registry with the new position, preserving
        // any accumulated injury status from prior actions.
        RobotInfo existing = context.getServer().getColonyRobots().get(request.getRobotId());
        if (existing != null) {
            existing.setX(destinationX);
            existing.setY(destinationY);
        } else {
            context.getServer().getColonyRobots().put(
                    request.getRobotId(),
                    new RobotInfo(request.getRobotId(), robotType, destinationX, destinationY)
            );
        }

        // Build a simple success response indicating the move was successful.
        List<AffectedRobot> affectedRobots = new ArrayList<>();
        affectedRobots.add(new AffectedRobot(request.getRobotId(), request.getRobotType(), 0));
        return createSuccessResponse(request, new ArrayList<>(), affectedRobots);
    }

    /**
     * Validates if the given robot type is allowed to perform a move action.
     *
     * @param type the robot type to validate
     * @return {@code true} if the robot type is valid for movement; {@code false} otherwise
     */
    @Override
    public boolean isValidRobotType(RobotType type) {
        // All primary robot types are allowed to move.
        return type == RobotType.MINER ||
               type == RobotType.CARTOGRAPHER ||
               type == RobotType.FARMER ||
               type == RobotType.HARVESTER ||
               type == RobotType.PIPELINER;
    }

    /**
     * Checks if the destination cell is valid for movement for the given robot type.
     *
     * @param cell the destination cell to check
     * @param robotType the type of the robot attempting to move
     * @return {@code true} if the destination cell is valid; {@code false} otherwise
     */
    private boolean isValidDestination(Cell cell, RobotType robotType) {
        // Cartographers can move on any terrain.
        if (robotType == RobotType.CARTOGRAPHER) {
            return true;
        }
        // Other robots cannot move onto impassable terrains (e.g., IMPENETRABLE, WATER).
        if (robotType == RobotType.FARMER || robotType == RobotType.HARVESTER ||
            robotType == RobotType.PIPELINER || robotType == RobotType.MINER) {
            return cell.getType() != CellType.IMPENETRABLE && cell.getType() != CellType.WATER;
        }
        // By default, movement is not allowed.
        return false;
    }
}
