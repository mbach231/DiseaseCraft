package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LycanthropyDisease extends Disease {

    LycanthropyDisease() {
        name = "Lycanthropy";
        immunityTime = 3600 * 7; //cannot get for 1 week after curing
        initialize();
    }

    /*
     * Periodic Interval            (seconds)
     * Duration                     (seconds) [0 seconds = infinite duration]
     * Conact Transmission Rate     (%)
     * Airborne Transmission Rate   (%)
     * Transmission Distance        (%)
     * Death Cures                  (boolean)
     */
    @Override
    StageInfo createIncubationStage() {
        return new StageInfo(1200, 60 * 60 * 24, 0, 0, 0, false);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(600, 60 * 60 * 6, 0, 0, 0, false);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(60, 0, 10, 0, 0, false);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(300, 60 * 60 * 6, 0, 0, 0, false);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo(180, 1800, 0, 0, 0, false);
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 0));

    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 0));

    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        long worldTime = entity.getWorld().getTime();
        
        // If full moon
        if (EffectFunctions.isPhaseOfMoon(entity.getWorld().getFullTime(), EffectFunctions.MoonPhaseEn.FULL_MOON)
                && EffectFunctions.timeInRange(worldTime, 12700, 23500)) {
            entity.getWorld().playSound(entity.getLocation(), Sound.WOLF_GROWL, 2, 0);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0));
        }
        // Else if morning after full moon
        else if (EffectFunctions.isPhaseOfMoon(entity.getWorld().getFullTime(), EffectFunctions.MoonPhaseEn.WAXING_GIBBOUS)
                && EffectFunctions.timeInRange(worldTime, 0, 6000)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 30, 1));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 2));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 2, 0));
        }
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 1));
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 0));
    }

    @Override
    void initializeInfectableTypes() {

        addTypeCanInfectType(EntityType.PLAYER, EntityType.PLAYER);
    }

    @Override
    void initializeCures() {

        addCure(Stages.DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
               
        addCure(Stages.DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_ADVANCE2));
             
        addCure(Stages.DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Werewolf_Elixir));

        addCure(Stages.DiseaseStageEn.DECLINING, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));

        addCure(Stages.DiseaseStageEn.CONVALESCENT, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.CONVALESCENT, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

    }

    @Override
    void initializeVectors() {
        setVectorSpawnChance(EntityType.WOLF, 10);
        addVector(EntityType.WOLF, EntityType.PLAYER, 5, 0, 0);
    }
}
