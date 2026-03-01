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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.pojo.ActionParameters;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.pojo.AffectedRobot;
import fr.ensicaen.lv223.planet.pojo.DetectedCell;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;

/**
 * Handles requests to scan terrain at specific coordinates.
 * <p>
 * This handler validates scan requests, updates the visited status of cells,
 * and constructs a list of detected cells based on the surrounding area.
 * </p>
 *
 * @since 1.2
 */
public class ScanRequestHandler extends AbstractRequestHandler {
    private static final Logger logger = LogManager.getLogger(ScanRequestHandler.class);

    @Override
    protected String handleSpecificRequest(RequestContext context, ActionRequest request, ActionParameters params,
            int x, int y) throws JsonProcessingException {
        Cell cell = context.getServer().planet.getGrid()[y][x];

        // For non-cartographers, water or impenetrable cells are not valid for scanning.
        if ((cell.getType() == CellType.WATER || cell.getType() == CellType.IMPENETRABLE)
            && RobotType.fromString(request.getRobotType()) != RobotType.CARTOGRAPHER) {
            return createErrorResponse(request, ResponseType.INVALID_CELL);
        }

        cell.setVisited(true);

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

        // Build per-invocation lists (never shared between requests).
        List<DetectedCell> detectedCells = processScanRequest(x, y, context.getServer());
        List<AffectedRobot> affectedRobots = new ArrayList<>();
        affectedRobots.add(new AffectedRobot(request.getRobotId(), request.getRobotType(), 0));

        return createSuccessResponse(request, detectedCells, affectedRobots);
    }

    @Override
    protected boolean isValidRobotType(RobotType robotType) {
        // Valid robot types for scanning.
        return robotType == RobotType.CARTOGRAPHER || robotType == RobotType.FARMER 
                || robotType == RobotType.MINER || robotType == RobotType.HARVESTER 
                || robotType == RobotType.PIPELINER;
    }

    /**
     * Scans the 3×3 grid neighbourhood centred on {@code (x, y)} and returns
     * a fresh list of detected cells for this invocation.
     *
     * @param x      the x-coordinate of the scan centre
     * @param y      the y-coordinate of the scan centre
     * @param server the planet server instance
     * @return a new list of {@link DetectedCell} objects (never {@code null})
     */
    private List<DetectedCell> processScanRequest(int x, int y, PlanetServer server) {
        List<DetectedCell> detectedCells = new ArrayList<>();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int newX = x + dx;
                int newY = y + dy;
                DetectedCell detected = new DetectedCell(newX, newY);
                if (!server.isInvalidCell(newX, newY)) {
                    Cell gridCell = server.planet.getGrid()[newY][newX];
                    detected.setType(gridCell.getType());
                    detected.setUnits(gridCell.getUnits());
                } else {
                    detected.setType(CellType.UNKNOWN);
                    detected.setUnits(0);
                }
                detectedCells.add(detected);
            }
        }
        return detectedCells;
    }
}
