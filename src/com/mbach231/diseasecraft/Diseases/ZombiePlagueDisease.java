package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZombiePlagueDisease extends Disease {

    public ZombiePlagueDisease() {
        name = "Zombie Plague";
        immunityTime = 60;
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
        return new StageInfo(60, 1200, 25, 0, 10, true);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(60, 300, 25, 0, 10, true);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(50, 300, 25, 0, 10, true);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(60, 300, 25, 0, 10, true);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo(60, 180, 100, 0, 10, false);
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 0));

    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 1));

    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 2));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1));
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 1));
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));
        //entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
    }

    @Override
    void initializeInfectableTypes() {

        addTypeCanInfectType(EntityType.PLAYER, EntityType.PLAYER);
        addTypeCanInfectType(EntityType.PLAYER, EntityType.SHEEP);
        addTypeCanInfectType(EntityType.SHEEP, EntityType.SHEEP);
        addTypeCanInfectType(EntityType.SHEEP, EntityType.PLAYER);
    }

    @Override
    void initializeCures() {

        addCure(Stages.DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

        addCure(Stages.DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

        addCure(Stages.DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

        addCure(Stages.DiseaseStageEn.DECLINING, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.DECLINING, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

        addCure(Stages.DiseaseStageEn.CONVALESCENT, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(Stages.DiseaseStageEn.CONVALESCENT, CustomCures.getCure(CustomCures.CureEn.Antibiotic_CURE));

    }

    @Override
    void initializeVectors() {

        setVectorSpawnChance(EntityType.ZOMBIE, 20);

        addVector(EntityType.ZOMBIE, EntityType.PLAYER, 20, 0, 0);
        addVector(EntityType.ZOMBIE, EntityType.SHEEP, 50, 5, 1.5);
    }
}
