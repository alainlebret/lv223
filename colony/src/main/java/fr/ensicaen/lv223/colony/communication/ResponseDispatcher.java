/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.communication;

import fr.ensicaen.lv223.colony.pojo.ActionResponse;
import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.utils.Coordinate;

import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches responses received from the planet server to the appropriate response handler.
 * <p>
 * This class maintains a mapping from action names to their respective handlers. When a
 * response is received, it looks up the handler using the action name (converted to lower case)
 * and delegates the handling to that handler. If no specific handler exists for the action,
 * a default handler is used.
 * </p>
 */
public class ResponseDispatcher {

    /**
     * Mapping from action names (in lower case) to their corresponding response handlers.
     */
    private final Map<String, ResponseHandler> handlers;

    /**
     * Default response handler to be used when no specific handler is found.
     */
    private final ActionResponseHandler defaultHandler;

    /**
     * Constructs a ResponseDispatcher with the provided base coordinates.
     * The base coordinates are used by some handlers (e.g., MoveResponseHandler) to
     * translate global positions into local coordinates.
     *
     * @param baseX Global X coordinate of the colony base.
     * @param baseY Global Y coordinate of the colony base.
     */
    public ResponseDispatcher(int baseX, int baseY) {
        this.defaultHandler = new ActionResponseHandler();
        this.handlers = new HashMap<>();
        // Register specific response handlers for each action type.
        handlers.put(ActionType.SCAN.toString().toLowerCase(), new ScanResponseHandler(baseX, baseY));
        handlers.put(ActionType.MOVE.toString().toLowerCase(), new MoveResponseHandler(baseX, baseY));
        handlers.put(ActionType.HARVEST.toString().toLowerCase(), new HarvestResponseHandler());
        handlers.put(ActionType.MINE.toString().toLowerCase(), new MineResponseHandler());
        handlers.put(ActionType.CULTIVATE.toString().toLowerCase(), new CultivateResponseHandler());
        handlers.put(ActionType.PIPE.toString().toLowerCase(), new PipeResponseHandler());
        handlers.put(ActionType.PUMP.toString().toLowerCase(), new PumpResponseHandler());
        // Additional handlers can be added here as needed.
    }

    /**
     * Dispatches the given action response to the appropriate handler.
     * <p>
     * If the action response is null or if no specific handler is found for the action,
     * the default handler is used to process the response.
     * </p>
     *
     * @param response     The parsed {@link ActionResponse} from the server.
     * @param robot        The robot that initiated the action.
     * @param targetGlobal The target global coordinate associated with the action (if applicable).
     */
    public void dispatch(ActionResponse response, Robot robot, Coordinate targetGlobal) {
        if (response == null) {
            defaultHandler.processResponse(null);
            return;
        }
        String actionKey = response.getAction().toLowerCase();
        ResponseHandler handler = handlers.get(actionKey);
        if (handler != null) {
            handler.handleResponse(response, robot, targetGlobal);
        } else {
            defaultHandler.processResponse(response);
        }
    }
}
