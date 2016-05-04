package com.mbach231.diseasecraft.Stages;

import com.mbach231.diseasecraft.DiseaseEffects.DiseaseEffect;
import java.util.Set;
import org.bukkit.entity.LivingEntity;

public class StageInfo {
    
    /*
     * Periodic Interval            (seconds)
     * Duration                     (seconds) [0 seconds = infinite duration]
     * Conact Transmission Rate     (%)
     * Airborne Transmission Rate   (%)
     * Transmission Distance        (%)
     * Death Cures                  (boolean)
     */

    // Frequency of occurance in seconds
    private long periodicInterval;
    // Duration of stage in seconds (0 = infinite duration)
    private long duration;
    // Percent chance of successful transmission to other nearby entitys
    private double contactTransmissionRate;
    // Percent chance of successful transmission to other nearby entitys
    private double airborneTransmissionRate;
    // Max distance between 
    private double transmissionDistance;
    // If true, entity dying will cure disease
    private boolean deathCures;

    private Set<DiseaseEffect> diseaseEffectSet;
    
    public StageInfo() {
        duration = -1;
    }

    public StageInfo(int periodicInterval,
            long duration,
            double contactTransmissionRate,
            double airborneTransmissionRate,
            double transmissionDistance,
            boolean deathCures) {
        this.periodicInterval = periodicInterval;
        this.duration = duration;
        this.contactTransmissionRate = contactTransmissionRate;
        this.airborneTransmissionRate = airborneTransmissionRate;
        this.transmissionDistance = transmissionDistance;
        this.deathCures = deathCures;
    }

    public boolean skipStage() {
        return duration == -1;
    }

    public long getPeriodicInterval() {
        return periodicInterval;
    }

    public long getDuration() {
        return duration;
    }

    public double getAirborneTransmissionRate() {
        return airborneTransmissionRate;
    }
    
    public double getContactTransmissionRate() {
        return contactTransmissionRate;
    }

    public double getTransmissionDistance() {
        return transmissionDistance;
    }

    public boolean getDeathCures() {
        return deathCures;
    }
    
    public void applyEffects(LivingEntity entity) {
        
        for(DiseaseEffect diseaseEffect : diseaseEffectSet) {
            diseaseEffect.applyEffect(entity);
        }
        
    }
}
