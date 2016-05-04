package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LymeDisease extends Disease {

    public LymeDisease() {
        name = "Lyme Disease";
        immunityTime = 600;
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
        return new StageInfo(100, 600, 0, 0, 0, false);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(30, 600, 0, 0, 0, false);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(30, 900, 0, 0, 0, false);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(30, 0, 0, 0, 0, true);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo(10, 10, 0, 0, 0, false);
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 5, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 0));    
    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 15, 2));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 17, 2));   
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 29, 4));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 4));  
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
    }

    @Override
    void initializeInfectableTypes() {
        
        addTypeCanInfectType(EntityType.WOLF, EntityType.PLAYER);
        addTypeCanInfectType(EntityType.OCELOT, EntityType.PLAYER);
        
    }

    @Override
    void initializeCures() {

        addCure(Stages.DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
               
        addCure(Stages.DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));

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
        //setVectorSpawnChance(EntityType.PIG, 0.01);
       // addVector(EntityType.PIG, EntityType.PLAYER, 0, 25, 2);
    }
}
