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
 * Handles requests to harvest cultivated crops.
 * <p>
 * This handler validates harvesting requests, ensuring that the targeted cell is
 * suitable for harvesting and that the requested quantity is valid. If valid, it
 * deducts the harvested units from the cell and updates the colony robot information.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class HarvestRequestHandler extends AbstractRequestHandler {

    public static final int MAX_CROPS_HARVESTED_PER_CELL = 1000;

    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request, ActionParameters params,
            int x, int y) throws JsonProcessingException {

        // Retrieve the target cell based on the given coordinates.
        Cell cell = context.getServer().planet.getGrid()[y][x];
        if (!isSuitableForHarvesting(cell)) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        int unitsToHarvest = params.getUnits();

        if (!isValidQuantity(unitsToHarvest)) {
            return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
        }

        if (!hasEnoughUnitsToHarvest(cell, unitsToHarvest)) {
            return createErrorResponse(request, ResponseType.INVALID_QUANTITY);
        }

        // Harvest the requested units from the cell.
        harvestFromCell(cell, unitsToHarvest);

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

        // Return a success response.
        return createSuccessResponse(request, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public boolean isValidRobotType(RobotType type) {
        return type == RobotType.HARVESTER;
    }

    /**
     * Checks whether the given cell is suitable for harvesting.
     * <p>
     * A cell is considered suitable if its type is FRUITS_AND_VEGETABLES.
     * </p>
     *
     * @param cell the cell to check
     * @return {@code true} if the cell is suitable; {@code false} otherwise
     */
    private boolean isSuitableForHarvesting(Cell cell) {
        return cell.getType() == CellType.FRUITS_AND_VEGETABLES;
    }

    /**
     * Validates the harvest quantity.
     *
     * @param quantity the requested harvest quantity
     * @return {@code true} if the quantity is between 0 and the maximum allowed; {@code false} otherwise
     */
    private boolean isValidQuantity(int quantity) {
        return quantity >= 0 && quantity <= MAX_CROPS_HARVESTED_PER_CELL;
    }

    /**
     * Checks if the cell has enough units available to be harvested.
     *
     * @param cell  the cell from which to harvest
     * @param units the requested harvest units
     * @return {@code true} if the cell has at least the requested units; {@code false} otherwise
     */
    private boolean hasEnoughUnitsToHarvest(Cell cell, int units) {
        return cell.getUnits() >= units;
    }

    /**
     * Harvests the specified number of units from the cell.
     * <p>
     * If the cell's resources are fully depleted after harvesting, the cell type is
     * changed to DRY_PRAIRIE.
     * </p>
     *
     * @param cell  the cell from which to harvest
     * @param units the number of units to harvest
     */
    private void harvestFromCell(Cell cell, int units) {
        cell.setUnits(cell.getUnits() - units);
        if (cell.getUnits() <= 0) {
            cell.setType(CellType.DRY_PRAIRIE); // Convert cell type after depletion
            cell.setUnits(0);
        }
        cell.setVisited(true);
    }
}
