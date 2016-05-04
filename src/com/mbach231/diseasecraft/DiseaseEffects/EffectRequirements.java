
package com.mbach231.diseasecraft.DiseaseEffects;

import com.mbach231.diseasecraft.Diseases.EffectFunctions;
import org.bukkit.World;

public class EffectRequirements {

    private int requiredStartTime = -1;
    private int requiredEndTime = -1;
    private EffectFunctions.MoonPhaseEn requiredMoonPhase = EffectFunctions.MoonPhaseEn.NO_PHASE;

    EffectRequirements(int requiredStartTime,
            int requiredEndTime,
            EffectFunctions.MoonPhaseEn requiredMoonPhase) {

        this.requiredStartTime = requiredStartTime;
        this.requiredEndTime = requiredEndTime;
        this.requiredMoonPhase = requiredMoonPhase;
    }

    EffectRequirements(int requiredStartTime,
            int requiredStopTime) {
        this.requiredStartTime = requiredStartTime;
        this.requiredEndTime = requiredStopTime;
    }

    EffectRequirements(EffectFunctions.MoonPhaseEn requiredMoonPhase) {
        this.requiredMoonPhase = requiredMoonPhase;
    }

    EffectRequirements() {
    }

    public boolean meetsRequirements(World world) {
        if (requiredTime()) {
            if (timeInRange(world.getTime()) == false) {
                return false;
            }
        }

        if (requiredMoonPhase()) {
            if (isPhaseOfMoon(world.getFullTime()) == false) {
                return false;
            }
        }

        return true;
    }

    private boolean timeInRange(long time) {
        return EffectFunctions.timeInRange(time, requiredStartTime, requiredEndTime);
    }

    private boolean isPhaseOfMoon(long time) {
        return EffectFunctions.isPhaseOfMoon(time, requiredMoonPhase);
    }

    private boolean requiredTime() {
        return requiredStartTime != -1 && requiredEndTime != -1;
    }

    private boolean requiredMoonPhase() {
        return requiredMoonPhase != EffectFunctions.MoonPhaseEn.NO_PHASE;
    }
}
