
package com.mbach231.diseasecraft.DiseaseEffects;

import org.bukkit.entity.LivingEntity;


abstract public class DiseaseEffect {

    DiseaseEffect(){
    }
    
    abstract public void applyEffect(LivingEntity entity);

}
