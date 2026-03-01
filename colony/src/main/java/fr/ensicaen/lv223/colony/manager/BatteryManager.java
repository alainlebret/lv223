/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.colony.manager;

/**
 * Utility class for managing battery consumption.
 * <p>
 * This class provides methods to compute the daily battery consumption
 * and to update the battery level based on the activity performed.
 * </p>
 */
public final class BatteryManager {
    public static final int E_MAX = 40;

    private BatteryManager() {}

    public static double toEnergyUnits(int percent) {
        percent = clamp(percent, 0, 100);
        return E_MAX * (percent / 100.0);
    }

    public static double consume(double energyUnits, double alpha) {
        double e = Math.max(0.0, Math.min(E_MAX, energyUnits));
        return Math.max(0.0, e - alpha);
    }

    public static int toPercent(double energyUnits) {
        double e = Math.max(0.0, Math.min(E_MAX, energyUnits));
        return (int)Math.round(100.0 * e / E_MAX);
    }

    public static int recharge() {
        return E_MAX;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
    private static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

}
