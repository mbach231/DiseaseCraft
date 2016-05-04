package com.mbach231.diseasecraft.Vectors;

import com.mbach231.diseasecraft.Diseases.Disease;
import org.bukkit.entity.EntityType;

public class DiseaseVector {

    private EntityType vectorType;
    private EntityType infectableByVectorType;
    private double contactTransmissionRate;
    private double airborneTransmissionRate;
    private double airborneTransmissionDistance;
    private Disease disease;

    DiseaseVector(EntityType vectorType, EntityType infectableByVectorType, double contactRate, double airborneRate, double airborneDistance, Disease disease) {
        this.vectorType = vectorType;
        this.infectableByVectorType = infectableByVectorType;
        this.contactTransmissionRate = contactRate;
        this.airborneTransmissionRate = airborneRate;
        this.airborneTransmissionDistance = airborneDistance;
        this.disease = disease;
    }

    public EntityType getVectorType() {
        return vectorType;
    }
    
    public EntityType getInfecatbleByVectorType() {
        return infectableByVectorType;
    }
    
    public double getContactTransmissionRate() {
        return contactTransmissionRate;
    }

    public double getAirborneTransmissionRate() {
        return airborneTransmissionRate;
    }

    public double getAirborneTransmissionDistance() {
        return airborneTransmissionDistance;
    }

    public Disease getDisease() {
        return disease;
    }
}
