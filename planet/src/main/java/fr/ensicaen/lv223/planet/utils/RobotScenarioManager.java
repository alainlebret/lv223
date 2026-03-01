/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.utils;

import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.CellType;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.server.PlanetServer;

/**
 * Manages and executes different robot interaction scenarios within the
 * lv223 planetary simulation environment. It controls the setup and
 * execution of various demos, each showcasing specific robot behaviors 
 * and interactions in the simulation.
 *
 * @since 1.0 (lv223 simulation project)
 */
public class RobotScenarioManager {

    /** The logger for the {@code RobotScenarioManager} class */
    static final Logger logger = LogManager.getLogger(RobotScenarioManager.class);

    /** The random number generator for the manager */
    Random random = new Random();

    /** The PlanetServer instance for the manager */
    PlanetServer server;

    /** The map of simulated robots */
    Map<String, RobotInfo> simulatedRobots;

    /**
     * Creates a new demo factory with the specified planet server.
     *
     * @param server The planet server for the demo factory.
     */
    public RobotScenarioManager(PlanetServer server) {
        this.server = server;
        simulatedRobots = this.server.getColonyRobots();
    }

    /**
     * Executes a given scenario on the specified planet.
     *
     * @param demoType The scenario or demo to run.
     * @param planet   The planet where the demo is executed.
     */
    public void runScenario(RobotScenarioType demoType, Planet planet) {
        // Clear previous scenario robots
        this.simulatedRobots.clear();

        switch (demoType) {
            case SEE:
                moveCartographerOnClearPlanet(planet);
                break;
            case MOVE:
                move(planet);
                break;
            case MOVE_AND_SCAN:
                moveAndScan(planet);
                break;
            case MOVE_AND_CULTIVATE:
                moveAndCultivate(planet);
                break;
            case MOVE_AND_HARVEST:
                moveAndHarvest(planet);
                break;
            case MOVE_AND_PIPE:
                moveAndPipe(planet);
                break;
            case MOVE_AND_MINE:
                moveAndMine(planet);
                break;
            case PUMP:
                pump(planet);
                break;
            case VARIOUS:
                moveAndAct(planet);
                break;
            default:
                throw new IllegalArgumentException("Unknown scenario type: " + demoType);
        }
    }

    /**
     * Initializes a cartographer at the base and moves it randomly to a new
     * position.
     *
     * @param planet The planet on which the robot is initialized and moved.
     */
    private void moveCartographerOnClearPlanet(Planet planet) {
        displayWhole(planet);
        initializeRobots(planet, RobotType.CARTOGRAPHER, 1);
        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
        }
    }

    /**
     * Initializes 5 cartographers at the base and move each robot to a new position.
     *
     * @param planet The planet on which the robots are initialized and moved
     */
    private void move(Planet planet) {
        initializeRobots(planet, RobotType.CARTOGRAPHER, 5);
        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
        }
    }

    /**
     * Initializes 5 robots at the base, moves each robot to a new position, and
     * scans neighborhood.
     *
     * @param planet The planet on which the robots will be initialized.
     */
    private void moveAndScan(Planet planet) {
        // Initialize robots at different positions
        initializeRobots(planet, RobotType.CARTOGRAPHER, 5);
        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            int newX = entry.getValue().getX();
            int newY = entry.getValue().getY();
            Cell cell = planet.getGrid()[newY][newX];
            String id = entry.getValue().getId();
            String type = entry.getValue().getType().toFormattedString();

            String request = createScanRequest(id, type, newX, newY);
            String answer = server.answerToRequest(request);
            if (!answer.isEmpty()) {
                logger.debug("{} / {}", request, answer);
            } else {
                break;
            }
        }
    }

    /**
     * Initializes 5 robots at the base, moves each robot to a new position, 
     * and cultivates the cell if its type is a prairie.
     * @param planet The planet on which the robots will act.
     */
    private void moveAndCultivate(Planet planet) {
        // Initialize robots at different positions
        initializeRobots(planet, RobotType.FARMER, 5);

        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            if (doTask(planet, entry))
                break;
        }
    }

    /**
     * Initializes 5 robots at the base, moves each robot to a new position,
     * and picks food if present at the new position.
     *
     * @param planet the planet on which the robots will be initialized and moved
     */
    private void moveAndHarvest(Planet planet) {
        initializeRobots(planet, RobotType.HARVESTER, 5);

        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            if (doTask(planet, entry))
                break;
        }
    }

    /**
     * Initializes 5 robots at the base, moves each robot to a new position, 
     * and constructs pipeline if necessary at the new position.
     *
     * @param planet The planet on which the robots will act.
     */
    private void moveAndPipe(Planet planet) {
        initializeRobots(planet, RobotType.PIPELINER, 5);

        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            if (doTask(planet, entry))
                break;
        }
    }

    /**
     * Initializes 5 robots at the base, moves each robot to a new position, and
     * mines if necessary at the new position.
     * @param planet The planet on which the robots will act.
     */
    private void moveAndMine(Planet planet) {
        initializeRobots(planet, RobotType.MINER, 5);

        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            if (doTask(planet, entry))
                break;
        }
    }

    /**
     * Pumps water from .
     * @param planet The planet on which the robots will act.
     */
    private void pump(Planet planet) {
        setAlienConstructionOnValidCells(planet);
        displayWhole(planet);
        initializeRobots(planet, RobotType.PIPELINER, 2);
        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            int newX = entry.getValue().getX();
            int newY = entry.getValue().getY();
            Cell cell = planet.getGrid()[newY][newX];
            String id = entry.getValue().getId();
            String type = entry.getValue().getType().toFormattedString();

            if (type.equals(RobotType.PIPELINER.toFormattedString()) || type.equals(RobotType.FARMER.toFormattedString())) {
                String request = createPumpRequest(id, type, 3, 0);
                String answer = server.answerToRequest(request);
                if (!answer.isEmpty()) {
                    logger.debug("{} / {}", request, answer);
                }
            }
        }
        resetAlienConstructionOnValidCells(planet);

    }

    /**
     * Moves and acts.
     * @param planet
     */
    private void moveAndAct(Planet planet) {
        RobotType[] robotTypes = { RobotType.CARTOGRAPHER, RobotType.FARMER, RobotType.MINER, RobotType.PIPELINER,
                RobotType.HARVESTER };

        initializeRobots(planet, RobotType.UNKNOWN, 5);

        for (Map.Entry<String, RobotInfo> entry : simulatedRobots.entrySet()) {
            moveRobot(planet, entry);
            // TODO MORE ACTIONS
        }
    }

    /**
     * Initializes a given number of robots.
     * @param planet The planet on which the robots will be initialized.
     * @param type The type of robots.
     * @param numberOfRobots The number of robots.
     */
    private void initializeRobots(Planet planet, RobotType type, int numberOfRobots) {
        RobotType[] robotTypes = { RobotType.CARTOGRAPHER, RobotType.FARMER, RobotType.MINER, RobotType.PIPELINER,
                RobotType.HARVESTER };

        // Initialize robots at different positions
        for (int i = 0; i < numberOfRobots; i++) {
            RobotInfo robot;
            String id = "r" + i;
            RobotType robotType = (type == RobotType.UNKNOWN) ? robotTypes[i % robotTypes.length] : type;

            int x, y;
            Cell cell;
            do {
                x = random.nextInt(planet.getWidth());
                y = random.nextInt(planet.getHeight());
                cell = planet.getGrid()[y][x];
                // Retry if the cell is not suitable for the robot type
            } while (!isCellSuitableForRobotType(cell, robotType));

            robot = new RobotInfo(id, robotType, x, y);
            simulatedRobots.put(id, robot);
        }
    }

    /**
     * Checks if a given cell is suitable for a given robot type.
     * @param cell  The cell to check.
     * @param robotType The robot type.
     * @return  True if the cell is suitable, false otherwise.
     */
    private boolean isCellSuitableForRobotType(Cell cell, RobotType robotType) {
        // Cartographers can go anywhere
        if (robotType == RobotType.CARTOGRAPHER) {
            return true;
        }
        // Other robots cannot go on Water or Impenetrable cells
        return cell.getType() != CellType.WATER && cell.getType() != CellType.IMPENETRABLE;
    }

    /**
     * Displays the whole planet.
     */
    private static void displayWhole(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.setVisited(true);
            }
        }
    }

    /**
     * Moves a robot to a new random position.
     * @param planet The planet on which the robot will be moved.
     * @param entry The robot to move.
     */
    private void moveRobot(Planet planet, Map.Entry<String, RobotInfo> entry) {
        RobotInfo robot = entry.getValue();
        String id = robot.getId();
        String type = robot.getType().toFormattedString();

        // Current robot position
        int x = robot.getX();
        int y = robot.getY();

        // Choose a new random position
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;
        int newX = Math.max(0, Math.min(x + dx, planet.getWidth() - 1));
        int newY = Math.max(0, Math.min(y + dy, planet.getHeight() - 1));

        String request = createMoveRequest(id, type, x, y, newX, newY);
        String answer = server.answerToRequest(request);
        if (answer.contains("success")) {
            logger.debug("{} / {}", request, answer);
            robot.setX(newX);
            robot.setY(newY);
        }
    }

    /**
     * Performs a task for a given robot.
     * 
     * @param planet The planet on which the robot will be moved.
     * @param entry The robot to perform a task for.
     * @return True if the task was performed successfully, false otherwise.
     */
    private boolean doTask(Planet planet, Map.Entry<String, RobotInfo> entry) {
        int newX = entry.getValue().getX();
        int newY = entry.getValue().getY();
        Cell cell = planet.getGrid()[newY][newX];
        String id = entry.getValue().getId();
        String type = entry.getValue().getType().toFormattedString();

        if (cell.getType() == CellType.FRUITS_AND_VEGETABLES && type.equals(RobotType.HARVESTER.toFormattedString())) {
            String request = createHarvestRequest(id, type, newX, newY);
            String answer = server.answerToRequest(request);
            if (!answer.isEmpty()) {
                logger.debug("{} / {}", request, answer);
            } else {
                return true;
            }
        } else if ((cell.getType() == CellType.WET_PRAIRIE || cell.getType() == CellType.PRAIRIE
                || cell.getType() == CellType.DRY_PRAIRIE) &&
                type.equals(RobotType.FARMER.toFormattedString())) {
            String request = createCultivateRequest(id, type, newX, newY);
            String answer = server.answerToRequest(request);
            if (!answer.isEmpty()) {
                logger.debug("{} / {}", request, answer);
            } else {
                return true;
            }
        } else if ((cell.getType() != CellType.IMPENETRABLE && cell.getType() != CellType.WATER
                && cell.getType() != CellType.BASE) && type.equals(RobotType.PIPELINER.toFormattedString())) {
            String request = createPipeRequest(id, type, newX, newY);
            String answer = server.answerToRequest(request);
            if (!answer.isEmpty()) {
                logger.debug("{} / {}", request, answer);
            } else {
                return true;
            }
        } else if (cell.getType() == CellType.MINERAL && type.equals(RobotType.MINER.toFormattedString())) {
            String request = createMineRequest(id, type, newX, newY);
            String answer = server.answerToRequest(request);
            if (!answer.isEmpty()) {
                logger.debug("{} / {}", request, answer);
            } else {
                return true;
            }
        }
        return false;
    }

    private String createMoveRequest(String id, String type, int x, int y, int newX, int newY) {
        return "{ \"action\": \"move\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + ", \"newX\": " + newX + ", \"newY\": " + newY
                + " } }";
    }

    private String createScanRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"scan\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + " } }";
    }

    private String createCultivateRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"cultivate\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + " } }";
    }

    private String createHarvestRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"harvest\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + ", \"units\": " + 10 + " } }";
    }

    private String createPipeRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"pipe\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + " } }";
    }

    private String createMineRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"mine\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + ", \"units\": " + 10 + " } }";
    }

    private String createPumpRequest(String id, String type, int x, int y) {
        return "{ \"action\": \"pump\", \"robotId\": \"" + id + "\", \"robotType\": \"" + type
                + "\", \"parameters\": { \"x\": " + x + ", \"y\": " + y + ", \"units\": " + 10 + " } }";
    }

    /**
     * Sets alien construction (pipeline) on all valid cells in the planet grid.
     * Valid cells are those that are not water or impenetrable.
     * 
     * @param planet The planet whose cells are to be updated.
     */
    private void setAlienConstructionOnValidCells(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (Cell[] element : grid) {
            for (Cell cell : element) {
                // Check if the cell is neither water nor impenetrable
                if (cell.getType() != CellType.WATER && cell.getType() != CellType.IMPENETRABLE) {
                    cell.setHasAlienConstructionOnIt(true);
                }
            }
        }
    }

    /**
     * Reset alien construction (pipeline) on all valid cells in the planet grid.
     * Valid cells are those that are not water or impenetrable.
     * 
     * @param planet The planet whose cells are to be updated.
     */
    private void resetAlienConstructionOnValidCells(Planet planet) {
        Cell[][] grid = planet.getGrid();
        for (Cell[] element : grid) {
            for (Cell cell : element) {
                // Check if the cell is neither water nor impenetrable
                if (cell.getType() != CellType.WATER && cell.getType() != CellType.IMPENETRABLE) {
                    cell.setHasAlienConstructionOnIt(false);
                }
            }
        }
    }

}
