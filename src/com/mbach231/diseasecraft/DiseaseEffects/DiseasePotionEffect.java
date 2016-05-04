package com.mbach231.diseasecraft.DiseaseEffects;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class DiseasePotionEffect extends DiseaseEffect {

    private EffectRequirements effectRequirements;
    private PotionEffect potionEffect;

    DiseasePotionEffect(EffectRequirements effectRequirements,
            PotionEffect potionEffect) {
        this.effectRequirements = effectRequirements;
        this.potionEffect = potionEffect;
    }

    @Override
    public void applyEffect(LivingEntity entity) {

        // If in correct conditons for this effect
        if (effectRequirements.meetsRequirements(entity.getWorld())) {
            // Apply potion effect
            entity.addPotionEffect(potionEffect);
        }
    }
}
