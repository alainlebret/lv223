/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.colony.decision.LocalMap;
import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.pojo.DetectedCell;
import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.utils.Cell;
import fr.ensicaen.lv223.colony.utils.CellData;
import fr.ensicaen.lv223.colony.utils.CellType;

/**
 * Handles the response from the server after a scan action request.
 * <p>
 * Detected cell coordinates are received in global (server) space and translated
 * to local (colony-relative) space before being stored in the robot’s {@link LocalMap},
 * so that the map remains consistent with {@code robot.getCurrentLocation()} and
 * {@link LocalMap#getImmediateEnvironment(Coordinate)}.
 * </p>
 */
public class ScanResponseHandler extends AbstractResponseHandler {
    private static final Logger logger = LogManager.getLogger(ScanResponseHandler.class);

    /** Global X coordinate of the colony base. */
    private final int baseX;

    /** Global Y coordinate of the colony base. */
    private final int baseY;

    /**
     * Constructs a ScanResponseHandler with the provided base coordinates.
     *
     * @param baseX global X coordinate of the colony base
     * @param baseY global Y coordinate of the colony base
     */
    public ScanResponseHandler(int baseX, int baseY) {
        this.baseX = baseX;
        this.baseY = baseY;
    }

    @Override
    protected void updateRobotState(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        List<DetectedCell> detectedCells = response.getDetectedCells();

        if (detectedCells != null && !detectedCells.isEmpty()) {
            logger.info("Robot {} detected {} cells.", robot.getName(), detectedCells.size());
            LocalMap localMap = robot.getLocalMap();
            for (DetectedCell dc : detectedCells) {
                CellType cellType = (dc.getType() != null) ? dc.getType() : CellType.UNKNOWN;
                // Translate global coordinate from server to local colony coordinate.
                Coordinate localCoord = RobotEnvironmentFacade.translateToLocalStatic(
                        new Coordinate(dc.getX(), dc.getY()), baseX, baseY);
                CellData cellData = new CellData(cellType, dc.getUnits());
                Cell newCell = new Cell(localCoord, cellType, cellData);
                localMap.updateCell(localCoord, newCell);
            }
        } else {
            logger.info("Robot {} detected no new cells.", robot.getName());
        }
    }
}
