package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VampirismDisease extends Disease {

    public VampirismDisease() {
        name = "Vampirism";
        immunityTime = 60 * 60 * 24;
        initialize();
    }

    @Override
    StageInfo createIncubationStage() {
        return new StageInfo(60, 90, 0, 0, 0, false);
    }

    @Override
    StageInfo createProdromalStage() {
        return new StageInfo(30, 90, 0, 0, 0, false);
    }

    @Override
    StageInfo createAcuteStage() {
        return new StageInfo(10, 0, 0, 0, 0, false);
    }

    @Override
    StageInfo createDecliningStage() {
        return new StageInfo(30, 600, 0, 0, 0, false);
    }

    @Override
    StageInfo createConvalescentStage() {
        return new StageInfo();
    }

    @Override
    void applyIncubationEffects(LivingEntity entity) {
        long worldTime = entity.getWorld().getTime();

        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 3, 0));
        if (EffectFunctions.timeInRange(worldTime, 23500, 12700)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 3, 0));
        }

    }

    @Override
    void applyProdromalEffects(LivingEntity entity) {
        long worldTime = entity.getWorld().getTime();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 2));
        if (EffectFunctions.timeInRange(worldTime, 23500, 12700)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
        }
    }

    @Override
    void applyAcuteEffects(LivingEntity entity) {
        long worldTime = entity.getWorld().getTime();

        if (EffectFunctions.timeInRange(worldTime, 13000, 23000)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 1));

            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setWalkSpeed((float) 0.25);
            } else {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 4, 1));
            }

        } else if (EffectFunctions.timeInRange(worldTime, 23500, 12700)) {

            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
            if (entity.getLocation().getBlock().getLightFromSky() >= 13) {
                entity.setFireTicks(20 * 9);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 8, 2));
            }

            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setWalkSpeed((float) 0.2);
            } 
        }
    }

    @Override
    void applyDecliningEffects(LivingEntity entity) {
        long worldTime = entity.getWorld().getTime();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
        if (EffectFunctions.timeInRange(worldTime, 23500, 12700)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
        }
    }

    @Override
    void applyConvalescentEffects(LivingEntity entity) {
    }

    @Override
    void initializeInfectableTypes() {

        //addTypeCanInfectType(EntityType.PLAYER,EntityType.PLAYER);
        addInfectableType(EntityType.PLAYER);
    }

    @Override
    void initializeCures() {

        addCure(DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(DiseaseStageEn.INCUBATION, CustomCures.getCure(CustomCures.CureEn.Antibiotic_ADVANCE2));

        addCure(DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Strong_Antibiotic_CURE));
        addCure(DiseaseStageEn.PRODROMAL, CustomCures.getCure(CustomCures.CureEn.Antibiotic_ADVANCE1));

        //addCure(DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Vampires_Draught));
        addCure(DiseaseStageEn.ACUTE, CustomCures.getCure(CustomCures.CureEn.Vampire_Elixir));
    }

    @Override
    void initializeVectors() {
        setVectorSpawnChance(EntityType.BAT, 1);
        addVector(EntityType.BAT, EntityType.PLAYER, 0, 5, 1.5);
    }
}
