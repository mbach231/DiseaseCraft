package com.mbach231.diseasecraft.DiseaseEffects;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class SoundEffect extends DiseaseEffect {

    private EffectRequirements effectRequirements;
    Sound sound;
    float volume;
    float pitch;

    SoundEffect(EffectRequirements effectRequirements,
            Sound sound,
            float volume,
            float pitch) {
        this.effectRequirements = effectRequirements;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void applyEffect(LivingEntity entity) {
        // If in correct conditons for this effect
        if (effectRequirements.meetsRequirements(entity.getWorld())) {
            entity.getWorld().playSound(entity.getLocation(), sound, volume, pitch);
        }
    }
}
