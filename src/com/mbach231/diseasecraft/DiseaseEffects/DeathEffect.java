package com.mbach231.diseasecraft.DiseaseEffects;

import org.bukkit.entity.LivingEntity;

public class DeathEffect extends DiseaseEffect {

    private EffectRequirements effectRequirements;
    private int percentChance;

    DeathEffect(EffectRequirements effectRequirements,
            int chance) {
        this.effectRequirements = effectRequirements;
        percentChance = chance;
    }

    @Override
    public void applyEffect(LivingEntity entity) {

        // If in correct conditons for this effect
        if (effectRequirements.meetsRequirements(entity.getWorld())) {
            if (Math.random() * 100 <= percentChance) {
                entity.damage(1000.0);
            }
        }
    }
}
