/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.decision;

import fr.ensicaen.lv223.colony.utils.Coordinate;
import fr.ensicaen.lv223.colony.decision.ExtendedLocalMap;
import fr.ensicaen.lv223.colony.decision.ExtendedLocalMap.CellRecord;

import java.util.*;

/**
 * Implements the A* algorithm for finding a path through an ExtendedLocalMap.
 * The heuristic used is Manhattan distance.
 */
public class AStarPathFinder {

    /**
     * Finds a path from the start coordinate to the goal coordinate using the A* algorithm.
     *
     * @param map   the extended local map representing the robot's memory of visited cells
     * @param start the starting coordinate
     * @param goal  the goal coordinate
     * @return a list of coordinates representing the path from start to goal, or an empty list if no path is found
     */
    public static List<Coordinate> findPath(ExtendedLocalMap map, Coordinate start, Coordinate goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<Coordinate> closedSet = new HashSet<>();
        Map<Coordinate, Double> gScore = new HashMap<>();
        Map<Coordinate, Coordinate> cameFrom = new HashMap<>();

        gScore.put(start, 0.0);
        openSet.add(new Node(start, heuristic(start, goal)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.coordinate.equals(goal)) {
                return reconstructPath(cameFrom, current.coordinate);
            }

            // Skip already-visited nodes (lazy deletion pattern for PriorityQueue).
            if (!closedSet.add(current.coordinate)) {
                continue;
            }

            for (Coordinate neighbor : getNeighbors(current.coordinate)) {
                if (closedSet.contains(neighbor) || !map.getMemory().containsKey(neighbor)) {
                    continue;
                }
                double tentativeGScore = gScore.get(current.coordinate) + movementCost(current.coordinate, neighbor);
                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current.coordinate);
                    gScore.put(neighbor, tentativeGScore);
                    double fScore = tentativeGScore + heuristic(neighbor, goal);
                    openSet.add(new Node(neighbor, fScore));
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Heuristic function: Manhattan distance.
     */
    private static double heuristic(Coordinate a, Coordinate b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Returns the movement cost between two adjacent coordinates.
     * For now, we assume a uniform cost (1 per step). This can be adapted to incorporate terrain or learned Q-values.
     */
    private static double movementCost(Coordinate a, Coordinate b) {
        return 1.0;
    }

    /**
     * Reconstructs the path from start to goal using the cameFrom map.
     */
    private static List<Coordinate> reconstructPath(Map<Coordinate, Coordinate> cameFrom, Coordinate current) {
        LinkedList<Coordinate> totalPath = new LinkedList<>();
        totalPath.addFirst(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.addFirst(current);
        }
        return totalPath;
    }

    /**
     * Returns the list of neighbor coordinates (8 directions) for a given coordinate.
     */
    private static List<Coordinate> getNeighbors(Coordinate coord) {
        List<Coordinate> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
                {1, 0}, {1, -1}, {0, -1}, {-1, -1}
        };
        for (int[] d : directions) {
            neighbors.add(new Coordinate(coord.getX() + d[0], coord.getY() + d[1]));
        }
        return neighbors;
    }

    /**
     * Helper class representing a node in the A* search.
     */
    private static class Node {
        Coordinate coordinate;
        double fScore;

        Node(Coordinate coordinate, double fScore) {
            this.coordinate = coordinate;
            this.fScore = fScore;
        }
    }
}