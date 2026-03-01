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
 * Handles requests to cultivate crops.
 * <p>
 * This request handler checks whether a given cell is suitable for cultivation,
 * applies crop cultivation (increasing the resource units), and updates the colony's
 * robot information accordingly.
 * </p>
 *
 * @since 1.0 (lv223-2024 simulation project)
 */
public class CultivateRequestHandler extends AbstractRequestHandler {

    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request, ActionParameters params,
            int x, int y) throws JsonProcessingException {

        // Retrieve the cell from the planet's grid based on provided coordinates.
        Cell cell = context.getServer().planet.getGrid()[y][x];
        if (!isSuitableForCultivation(cell)) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        // Apply crop cultivation on the cell.
        cultivateCrops(cell);

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

        // Return a success response with empty lists for affected robots and detected cells.
        return createSuccessResponse(request, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Checks whether the specified cell is suitable for cultivation.
     * A cell is considered suitable if its type is DRY_PRAIRIE, WET_PRAIRIE, or PRAIRIE.
     *
     * @param cell the cell to check
     * @return {@code true} if the cell is suitable for cultivation; {@code false} otherwise
     */
    private boolean isSuitableForCultivation(Cell cell) {
        CellType type = cell.getType();
        return type == CellType.DRY_PRAIRIE || type == CellType.WET_PRAIRIE || type == CellType.PRAIRIE;
    }

    /**
     * Applies crop cultivation effects to the given cell.
     * <p>
     * The method increases the cell's resource units based on a growth rate.
     * If the new resource level reaches or exceeds the maximum allowed, the cell
     * is converted to a FRUITS_AND_VEGETABLES type and marked as modified.
     * </p>
     *
     * @param cell the cell on which to apply cultivation
     */
    private void cultivateCrops(Cell cell) {
        int growthRate = determineGrowthRate(cell);
        int maxResourceUnits = Cell.MAX_RESOURCE_UNITS.get(CellType.FRUITS_AND_VEGETABLES);
        int newResourceUnits = Math.min(cell.getUnits() + growthRate, maxResourceUnits);

        // If the cell's resources are fully replenished, change its type.
        if (newResourceUnits >= maxResourceUnits) {
            cell.setType(CellType.FRUITS_AND_VEGETABLES);
            cell.setModified(true);
        }
        cell.setUnits(newResourceUnits);
        cell.setVisited(true);
    }

    /**
     * Determines the growth rate for crop cultivation based on the current cell type.
     *
     * @param cell the cell being cultivated
     * @return the growth rate (resource units added per cultivation action)
     */
    private int determineGrowthRate(Cell cell) {
        switch (cell.getType()) {
            case DRY_PRAIRIE:
                return 2;  // Slower growth rate on dry prairie.
            case WET_PRAIRIE:
                return 10; // Faster growth rate on wet prairie.
            default:
                return 8;  // Default growth rate for other suitable cell types.
        }
    }

    @Override
    public boolean isValidRobotType(RobotType type) {
        // Only robots of type FARMER are allowed to cultivate.
        return type == RobotType.FARMER;
    }
}
