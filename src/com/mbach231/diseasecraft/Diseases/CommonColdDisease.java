package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommonColdDisease extends Disease {

    public CommonColdDisease() {
        name = "Common Cold";
        immunityTime = 30000000;
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
        return new StageInfo(86400, 86400, 1, 0.1, 1, false);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(1000, 86400, 1, 1, 5, false);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(120, 86400, 10, 10, 10, false);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(1000, 86400, 1, 1, 5, false);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo(86400, 86400, 1, 1, 1, false);
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        entity.setHealth(entity.getHealth() - 1);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 1, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 1, 0));    
    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        entity.setHealth(entity.getHealth() - 1);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 3, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 0));   
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 1, 0));   
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        entity.setHealth(entity.getHealth() - 0.5);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 1, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 1, 0));
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
    }

    @Override
    void initializeInfectableTypes() {
        
        addTypeCanInfectType(EntityType.PLAYER, EntityType.PLAYER);
        
    }

    @Override
    void initializeCures() {
        
        addCure(DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_ADVANCE1));
        addCure(DiseaseStageEn.CONVALESCENT, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));

    }

    @Override
    void initializeVectors() {
        
        /*
         * Infected Type
         * Can Infect Type
         * % chance of being carrier (decided on entity spawn)
         * % chance of infecting via contact (attacking)
         * % chance of infecting via airborne (proximity)
         * Distance of infecting via airborne (meters)
         * 
         */
        setVectorSpawnChance(EntityType.PIG, 0.01);
        addVector(EntityType.PIG, EntityType.PLAYER, 0, 1, 3);
    }
}
