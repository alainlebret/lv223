/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.robot;

import fr.ensicaen.lv223.colony.communication.EnvironmentFeedback;
import fr.ensicaen.lv223.colony.communication.EnvironmentObserver;
import fr.ensicaen.lv223.colony.communication.RobotEnvironmentFacade;
import fr.ensicaen.lv223.colony.decision.LocalMap;
import fr.ensicaen.lv223.colony.manager.HealthManager;
import fr.ensicaen.lv223.colony.manager.NavigationManager;
import fr.ensicaen.lv223.colony.manager.SensingManager;
import fr.ensicaen.lv223.colony.strategy.DefaultRobotStrategy;
import fr.ensicaen.lv223.colony.strategy.GenericMaintenanceStrategy;
import fr.ensicaen.lv223.colony.strategy.RepairMaintenanceStrategy;
import fr.ensicaen.lv223.colony.strategy.RobotStrategy;
import fr.ensicaen.lv223.colony.utils.Coordinate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class representing a robot in the LV-223 colony simulation.
 * This class delegates navigation, sensing, and health management to dedicated
 * helper classes and supports dynamic strategy switching based on the robot's state.
 */
public abstract class Robot implements EnvironmentObserver {
    private static final Logger logger = LogManager.getLogger(Robot.class);

    private static final int LOW_BATTERY_THRESHOLD = 25;
    private static final int SEVERE_DAMAGE_THRESHOLD = 80;
    private static final String HEALTH_FCL_FILE = "health.fcl";
    private static final AtomicInteger ROBOT_COUNTER = new AtomicInteger(0);

    protected final String name;
    protected final RobotType type;
    protected final LocalMap localMap;
    protected Coordinate currentLocation;
    protected final RobotHealthData healthData;
    protected InjuryStatus injuryStatus;
    protected final RobotEnvironmentFacade environmentFacade;
    protected RobotStrategy strategy;

    // Helper managers
    protected final NavigationManager navigationManager;
    protected final SensingManager sensingManager;
    protected final HealthManager healthManager;

    // Fuzzy logic function block for health management
    protected final FunctionBlock functionBlock;

    /**
     * Creates a new Robot instance.
     *
     * @param type   the type of the robot
     * @param facade the RobotEnvironmentFacade used for server communication.
     */
    protected Robot(RobotType type, RobotEnvironmentFacade facade) {
        this.name = createUniqueName();
        this.type = type;
        this.environmentFacade = facade;
        this.environmentFacade.subscribe(this, this.name);

        // Initialize position at the colony base (local coordinates (0,0))
        this.currentLocation = new Coordinate(0, 0);
        this.localMap = new LocalMap(this.currentLocation);
        this.healthData = new RobotHealthData(100, 100, true, "Healthy");
        this.injuryStatus = new InjuryStatus();
        this.functionBlock = loadFisFile();

        // Instantiate helper managers
        this.navigationManager = new NavigationManager(this, facade);
        this.sensingManager = new SensingManager(this, facade);
        this.healthManager = new HealthManager(this, functionBlock);

        // Default strategy — each subclass overrides this in its own constructor.
        this.strategy = new DefaultRobotStrategy(this, facade);
    }

    /**
     * Executes the robot's task by performing state checks and delegating to the current strategy.
     * <ol>
     *     <li>Checks the battery level and, if too low, switches to a generic maintenance strategy.</li>
     *     <li>Checks the injury status and, if severe, switches to a repair maintenance strategy.</li>
     *     <li>If maintenance conditions have normalized while in maintenance mode,
     *         reverts to the default strategy.</li>
     *     <li>Finally, executes the current strategy.</li>
     * </ol>
     */
    public void performTask() {
        if (isBatteryLow()) {
            if (!(strategy instanceof GenericMaintenanceStrategy)) {
                logger.info("Energy low. Switching to GenericMaintenanceStrategy.");
                setStrategy(new GenericMaintenanceStrategy(this, environmentFacade));
            }
        } else if (isSeverelyInjured()) {
            if (!(strategy instanceof RepairMaintenanceStrategy)) {
                logger.info("Damage severe. Switching to RepairMaintenanceStrategy.");
                setStrategy(new RepairMaintenanceStrategy(this, environmentFacade));
            }
        } else if (strategy instanceof GenericMaintenanceStrategy || strategy instanceof RepairMaintenanceStrategy) {
            logger.info("Conditions normalized. Reverting to DefaultRobotStrategy.");
            setStrategy(new DefaultRobotStrategy(this, environmentFacade));
        }
        // Execute the current strategy.
        strategy.execute();
    }

    /**
     * Checks whether the robot's battery level is below the low threshold.
     *
     * @return true if the battery level is less than 25%; false otherwise.
     */
    private boolean isBatteryLow() {
        return healthData.getEnergyLevel() < LOW_BATTERY_THRESHOLD;
    }

    /**
     * Checks whether the robot is severely injured (either high mobility damage or degraded sensors).
     *
     * @return true if severe damage is detected; false otherwise.
     */
    private boolean isSeverelyInjured() {
        return injuryStatus.getMobilityDamage() >= SEVERE_DAMAGE_THRESHOLD || !injuryStatus.sensorsFunctional();
    }

    /**
     * Receives environment feedback and delegates processing to the appropriate manager.
     *
     * @param feedback the environment feedback.
     */
    @Override
    public void update(EnvironmentFeedback feedback) {
        switch (feedback.getType()) {
            case SCAN_RESULT:
                sensingManager.update(feedback);
                break;
            case INJURED:
                healthManager.update(feedback);
                break;
            default:
                logger.debug("Received unhandled feedback type: {}", feedback.getType());
                break;
        }
    }

    /**
     * Updates the robot's injury status based on sensor and mobility damage.
     *
     * @param sensorDamage   the sensor damage level.
     * @param mobilityDamage the mobility damage level.
     */
    public void updateInjuryStatus(int sensorDamage, int mobilityDamage) {
        this.injuryStatus.setSensorDamage(sensorDamage);
        this.injuryStatus.setMobilityDamage(mobilityDamage);
    }

    // -- Getters and setters --

    public String getName() {
        return name;
    }

    public RobotType getType() {
        return type;
    }

    public LocalMap getLocalMap() {
        return localMap;
    }

    public Coordinate getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Coordinate currentLocation) {
        this.currentLocation = currentLocation;
    }

    public RobotHealthData getHealthData() {
        return healthData;
    }

    public InjuryStatus getInjuryStatus() {
        return injuryStatus;
    }

    /**
     * Sets a new strategy for this robot.
     *
     * @param strategy the new RobotStrategy to be used.
     */
    public void setStrategy(RobotStrategy strategy) {
        this.strategy = strategy;
    }

    public RobotStrategy getStrategy() {
        return strategy;
    }

    // -- Private helper methods --

    /**
     * Generates a unique name for the robot using the current time and a random number.
     *
     * @return a unique robot name.
     */
    private String createUniqueName() {
        return String.format("r-%d-%d", System.currentTimeMillis(), ROBOT_COUNTER.getAndIncrement());
    }

    /**
     * Loads the FCL file for fuzzy logic health management.
     * This method uses the class loader to retrieve the resource from the classpath.
     *
     * @return the loaded FunctionBlock, or null if loading fails.
     */
    private FunctionBlock loadFisFile() {
        java.net.URL resource = getClass().getClassLoader().getResource(HEALTH_FCL_FILE);
        if (resource == null) {
            logger.error("FCL resource not found on classpath: {}", HEALTH_FCL_FILE);
            return null;
        }
        FIS fis = FIS.load(resource.getPath(), true);
        if (fis == null) {
            logger.error("Failed to load FCL file: {}", HEALTH_FCL_FILE);
            return null;
        }
        return fis.getFunctionBlock(null);
    }
}
