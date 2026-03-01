/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.manager;

import fr.ensicaen.lv223.colony.robot.Robot;
import fr.ensicaen.lv223.colony.communication.EnvironmentFeedback;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Manages health-related updates for a robot by using a Fuzzy Inference System (FIS).
 * <p>
 * This class is responsible for updating the robot's health status based on
 * external feedback (e.g., injury information). The FIS function block is used to
 * compute the new healthy level of the robot.
 * </p>
 */
public class HealthManager {
    private static final Logger logger = LogManager.getLogger(HealthManager.class);

    private static final String IN_ENERGY  = "energyLevel";
    private static final String IN_HPREV   = "inputHealthyLevel";
    private static final String IN_INJURY  = "injury";  // optional, but we will support it
    private static final String OUT_HNEW   = "outputHealthyLevel";

    /** The robot whose health is being managed. */
    private final Robot robot;
    
    /** The Fuzzy Inference System function block used to update the robot's health status. */
    private final FunctionBlock functionBlock;

    /**
     * Constructs a new HealthManager.
     *
     * @param robot the robot whose health is to be managed
     * @param functionBlock the Fuzzy Inference System function block used for computing health updates;
     *                      may be {@code null} if fuzzy logic is not available
     */
    public HealthManager(Robot robot, FunctionBlock functionBlock) {
        this.robot = robot;
        this.functionBlock = functionBlock;
    }

    /**
     * Updates the robot's health state based on the provided environment feedback.
     * <p>
     * This method uses the FIS function block to recalculate the robot's healthy level.
     * It sets the input variables "energyLevel" and "inputHealthyLevel", evaluates the FIS,
     * and then updates the robot's healthy level with the output variable "outputHealthyLevel".
     * </p>
     *
     * @param feedback the health-related feedback (e.g., injury information); ignored if the function block is {@code null}
     */
    public void update(EnvironmentFeedback feedback) {
        // injury gate (0/1) from actual colony feedback
        boolean injured = false;
        if (feedback != null && feedback.getInjuredRobots() != null) {
            injured = feedback.getInjuredRobots().contains(robot);
        }
        if (!injured) {
            return;
        }

        if (functionBlock == null) {
            logger.warn("Cannot update health for robot {}: FIS function block is not loaded.", robot.getName());
            // deterministic fallback (stable for sessions)
            int hPrev = clamp((int)Math.round(robot.getHealthData().getHealthyLevel()), 0, 100);
            robot.getHealthData().setHealthyLevel(clamp(hPrev - 5, 0, 100));
            return;
        }

        int c = clamp((int)Math.round(robot.getHealthData().getEnergyLevel()), 0, 100);
        int hPrev = clamp((int)Math.round(robot.getHealthData().getHealthyLevel()), 0, 100);

        // strict naming: if a variable is missing, log and still run (so you see the issue)
        if (functionBlock.getVariable(IN_ENERGY) == null) {
            logger.error("FCL mismatch: missing input variable '{}'", IN_ENERGY);
        } else {
            functionBlock.setVariable(IN_ENERGY, c);
        }

        if (functionBlock.getVariable(IN_HPREV) == null) {
            logger.error("FCL mismatch: missing input variable '{}'", IN_HPREV);
        } else {
            functionBlock.setVariable(IN_HPREV, hPrev);
        }

        // Optional input: only if declared in FCL
        if (functionBlock.getVariable(IN_INJURY) != null) {
            functionBlock.setVariable(IN_INJURY, 1);
        }

        functionBlock.evaluate();

        Variable out = functionBlock.getVariable(OUT_HNEW);
        if (out == null) {
            logger.error("FCL mismatch: missing output variable '{}'", OUT_HNEW);
            return;
        }

        int hNew = clamp((int)Math.round(out.getValue()), 0, 100);
        robot.getHealthData().setHealthyLevel(hNew);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
