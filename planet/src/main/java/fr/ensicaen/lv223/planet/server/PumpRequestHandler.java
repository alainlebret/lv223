/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.pojo.AffectedRobot;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Handles requests to pump water from water cells.
 * <p>
 * Validates water pumping requests, checks preconditions, and dispatches
 * robots to extract water if possible.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class PumpRequestHandler extends AbstractRequestHandler {

    public static final int MAX_PUMPED_WATER_PER_CELL = 500;

    /**
     * Handles a water pumping request.
     *
     * @param context The associated request context.
     * @param request The associated action request.
     * @param params  The action request parameters.
     * @param x       The x coordinate of the cell.
     * @param y       The y coordinate of the cell.
     * @return The response to the request.
     * @throws JsonProcessingException if an error occurs during JSON processing.
     */
    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request, ActionParameters params,
            int x, int y) throws JsonProcessingException {

        PlanetServer server = context.getServer();
        Cell cell = server.planet.getGrid()[y][x];

        // Validate that the cell is a water cell.
        if (cell.getType() != CellType.WATER) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        // Check if the cell is adjacent to a pipeline.
        if (!isAdjacentToPipeline(x, y, server)) {
            return createErrorResponse(request, ResponseType.NON_ADJACENT_CELL);
        }

        // Check if the cell is connected to the base via a pipeline (using BFS).
        if (!isConnectedToBaseViaPipeline(x, y, server)) {
            return createErrorResponse(request, ResponseType.NO_PIPELINE_CONNECTION);
        }

        int units = params.getUnits();

        // Validate that the requested quantity is within allowed limits.
        if (!isValidQuantity(units)) {
            return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
        }

        // Check if the cell has sufficient water units to pump.
        if (!hasEnoughUnitsToPump(cell, units)) {
            return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
        }

        // Calculate the radius for potential metamorphosis effects.
        int radius = calculateRadius(units);
        cell.triggerNeighboringCellMetamorphosis(server.planet, x, y, radius,
                server.planet.getMetamorphosisProbability());

        // Pump the water from the cell and record the event in the global water budget.
        pumpWaterFromCell(cell, units);
        server.planet.recordPumpEvent(units);

        // Determine the list of robots affected by the pump action.
        List<AffectedRobot> affectedRobots = findAffectedRobots(server, x, y, radius);
        affectedRobots.add(new AffectedRobot(request.getRobotId(), request.getRobotType(), 0));

        // Update the colony's robot registry with the new position, preserving
        // any accumulated injury status from prior actions.
        RobotInfo existing = server.getColonyRobots().get(request.getRobotId());
        if (existing != null) {
            existing.setX(x);
            existing.setY(y);
        } else {
            server.getColonyRobots().put(
                    request.getRobotId(),
                    new RobotInfo(request.getRobotId(), RobotType.fromString(request.getRobotType()), x, y));
        }

        // Return a success response with empty lists for detected cells.
        return createSuccessResponse(request, new ArrayList<>(), affectedRobots);
    }

    /**
     * Checks if the given robot type is valid for pumping water.
     *
     * @param type The robot type to check.
     * @return true if the robot type is valid, false otherwise.
     */
    public boolean isValidRobotType(RobotType type) {
        return type == RobotType.FARMER || type == RobotType.PIPELINER;
    }

    /**
     * Checks if the given quantity of water to be pumped is valid.
     *
     * @param quantity the quantity to check.
     * @return true if the quantity is valid, false otherwise.
     */
    boolean isValidQuantity(int quantity) {
        return quantity >= 0 && quantity <= MAX_PUMPED_WATER_PER_CELL;
    }

    /**
     * Checks if the cell is adjacent to a pipeline.
     *
     * @param x      the x-coordinate of the cell.
     * @param y      the y-coordinate of the cell.
     * @param server the server instance.
     * @return true if the cell is adjacent to a pipeline, false otherwise.
     */
    public boolean isAdjacentToPipeline(int x, int y, PlanetServer server) {
        Cell[][] grid = server.planet.getGrid();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue; // Skip the water cell itself
                }
                int checkX = x + i;
                int checkY = y + j;

                if (!server.isInvalidCell(checkX, checkY)) {
                    Cell adjacentCell = grid[checkY][checkX];
                    if (adjacentCell.hasAlienConstructionOnIt()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given cell is connected to the base via pipeline using BFS.
     *
     * @param x      the x-coordinate of the cell.
     * @param y      the y-coordinate of the cell.
     * @param server the server instance.
     * @return true if connected to the base, false otherwise.
     */
    private boolean isConnectedToBaseViaPipeline(int x, int y, PlanetServer server) {
        Cell[][] grid = server.planet.getGrid();
        boolean[][] visited = new boolean[server.planet.getHeight()][server.planet.getWidth()];
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            // If the base cell is found, return true.
            if (grid[current.y][current.x].getType() == CellType.BASE) {
                return true;
            }

            visited[current.y][current.x] = true;

            // Enqueue all unvisited neighboring cells that have a pipeline.
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nextX = current.x + i;
                    int nextY = current.y + j;

                    if (nextX < 0 || nextY < 0 || nextX >= server.planet.getWidth()
                            || nextY >= server.planet.getHeight() || visited[nextY][nextX]) {
                        continue;
                    }

                    if (grid[nextY][nextX].hasAlienConstructionOnIt()) {
                        queue.add(new Point(nextX, nextY));
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the cell has enough water units to pump.
     *
     * @param cell  the cell to check.
     * @param units the amount of water units requested.
     * @return true if the cell has enough water, false otherwise.
     */
    private boolean hasEnoughUnitsToPump(Cell cell, int units) {
        return (cell.getUnits() - units >= 0);
    }

    /**
     * Pumps water from the specified cell.
     *
     * @param cell  the cell from which to pump water.
     * @param units the number of water units to pump.
     */
    private void pumpWaterFromCell(Cell cell, int units) {
        cell.setUnits(cell.getUnits() - units);
        cell.setVisited(true);
    }

    /**
     * Calculates the extraction radius based on the pumped water quantity.
     *
     * @param units the quantity of water pumped.
     * @return the calculated extraction radius.
     */
    private int calculateRadius(int units) {
        return 1 + (units / MAX_PUMPED_WATER_PER_CELL);
    }

    /**
     * Finds the robots that are affected by the pump action.
     *
     * @param server  the server instance.
     * @param actionX the x-coordinate of the pump action.
     * @param actionY the y-coordinate of the pump action.
     * @param radius  the radius around the pump action.
     * @return a list of affected robots.
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
     * Checks if a robot is within a given radius of an action point.
     *
     * @param robotX  the robot's x-coordinate.
     * @param robotY  the robot's y-coordinate.
     * @param actionX the action's x-coordinate.
     * @param actionY the action's y-coordinate.
     * @param radius  the radius.
     * @return true if the robot is within the radius, false otherwise.
     */
    private boolean isWithinRadius(int robotX, int robotY, int actionX, int actionY, int radius) {
        int deltaX = actionX - robotX;
        int deltaY = actionY - robotY;
        int distanceSquared = deltaX * deltaX + deltaY * deltaY;
        return distanceSquared <= radius * radius;
    }

    /**
     * Calculates the injury inflicted on a robot due to the pump action.
     *
     * @param radius the radius of the pump action.
     * @return the calculated injury value.
     */
    private int calculateInjury(int radius) {
        int baseInjury = 10;
        return Math.max(0, baseInjury - radius * 2);
    }

    /**
     * A simple helper class to represent a point in the grid for BFS.
     */
    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
