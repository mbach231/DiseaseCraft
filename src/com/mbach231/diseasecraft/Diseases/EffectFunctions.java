package com.mbach231.diseasecraft.Diseases;

import org.bukkit.entity.LivingEntity;

public class EffectFunctions {

    public static enum MoonPhaseEn {
        FULL_MOON,
        WAXING_GIBBOUS,
        FIRST_QUARTER,
        WAXING_CRESENT,
        NEW_MOON,
        WANING_CRESENT,
        THIRD_QUARTER,
        WANING_GIBBOUS,
        NO_PHASE
    }

    public static boolean timeInRange(long time, long startTime, long endTime) {

        if (startTime > endTime) {
            if (time >= startTime || time <= endTime) {
                return true;
            }
        } else {
            if (time >= startTime && time <= endTime) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPhaseOfMoon(long time, MoonPhaseEn requiredMoonPhase) {
        return (((time / 24000) % 8) == getMoonPhase(requiredMoonPhase));
    }

    private static int getMoonPhase(MoonPhaseEn phase) {
        switch (phase) {
            case FULL_MOON:
                return 0;
            case  WAXING_GIBBOUS:
                return 1;
            case FIRST_QUARTER:
                return 2;
            case WAXING_CRESENT:
                return 3;
            case NEW_MOON:
                return 4;
            case WANING_CRESENT:
                return 5;
            case THIRD_QUARTER:
                return 6;
            case WANING_GIBBOUS:
                return 7;
            default:
                return -1;
        }
    }
}
