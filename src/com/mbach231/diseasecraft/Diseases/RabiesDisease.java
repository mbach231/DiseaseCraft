package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RabiesDisease extends Disease {

    public RabiesDisease() {
        name = "Rabies";
        immunityTime = 60 * 60 * 6;
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
        return new StageInfo(800, 3000, 69, 0, 0, false);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(600, 2800, 74, 0, 0, false);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(200, 7000, 80, 0, 0, false);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(400, 0, 70, 0, 0, true);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo();
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 16, 0));
    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 18, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 2, 0));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 11, 0));

        int percentChanceOfDeath = 10;
        if (Math.random() * 100 > percentChanceOfDeath) {
            entity.damage(1000.0);
        }
    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 24, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 12, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 2));
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 1));
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
    }

    @Override
    void initializeInfectableTypes() {

        addInfectableType(EntityType.PLAYER);

        addTypeCanInfectType(EntityType.WOLF, EntityType.PLAYER);
        addTypeCanInfectType(EntityType.WOLF, EntityType.OCELOT);
        addTypeCanInfectType(EntityType.WOLF, EntityType.BAT);
        addTypeCanInfectType(EntityType.WOLF, EntityType.WOLF);

        addTypeCanInfectType(EntityType.OCELOT, EntityType.PLAYER);
        addTypeCanInfectType(EntityType.OCELOT, EntityType.OCELOT);
        addTypeCanInfectType(EntityType.OCELOT, EntityType.BAT);
        addTypeCanInfectType(EntityType.OCELOT, EntityType.WOLF);

    }

    @Override
    void initializeCures() {

        addCure(DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(DiseaseStageEn.DECLINING, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
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
        setVectorSpawnChance(EntityType.BAT, 1);
        setVectorSpawnChance(EntityType.WOLF, 1);
        setVectorSpawnChance(EntityType.OCELOT, 1);

        addVector(EntityType.BAT, EntityType.PLAYER, 0, 10, 1);
        addVector(EntityType.BAT, EntityType.WOLF, 0, 10, 1);
        addVector(EntityType.BAT, EntityType.OCELOT, 0, 10, 1);

        addVector(EntityType.WOLF, EntityType.PLAYER, 0, 5, 1);
        addVector(EntityType.WOLF, EntityType.WOLF, 0, 5, 1);
        addVector(EntityType.WOLF, EntityType.OCELOT, 0, 5, 1);

        addVector(EntityType.OCELOT, EntityType.PLAYER, 0, 5, 1);
        addVector(EntityType.OCELOT, EntityType.WOLF, 0, 5, 1);
        addVector(EntityType.OCELOT, EntityType.OCELOT, 0, 5, 1);
    }
}
