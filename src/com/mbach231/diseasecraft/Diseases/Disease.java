package com.mbach231.diseasecraft.Diseases;

import com.mbach231.diseasecraft.Cures.Cure;
import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import com.mbach231.diseasecraft.Vectors.VectorManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

abstract public class Disease {

    // Name of disease
    String name;
    // Time (in seconds) that a player is immune to this disease after being cured
    long immunityTime;
    Map<DiseaseStageEn, StageInfo> diseaseStageMap;
    Map<DiseaseStageEn, Set<Cure>> cureMap;
    Set<EntityType> infectableTypeSet;
    Map<EntityType, Set<EntityType>> infectableTypeMap;
    Map<EntityType, Double> vectorSpawnChanceMap;
    
    public void initialize() {
        diseaseStageMap = new HashMap();
        cureMap = new HashMap();
        infectableTypeSet = new HashSet();
        infectableTypeMap = new HashMap();
        vectorSpawnChanceMap = new HashMap();
        initializeInfectableTypes();
        initializeCureMap();
        initializeDiseaseStageMap();
        initializeVectors();
    }

    private void initializeCureMap() {
        cureMap.put(DiseaseStageEn.INCUBATION, new HashSet());
        cureMap.put(DiseaseStageEn.PRODROMAL, new HashSet());
        cureMap.put(DiseaseStageEn.ACUTE, new HashSet());
        cureMap.put(DiseaseStageEn.DECLINING, new HashSet());
        cureMap.put(DiseaseStageEn.CONVALESCENT, new HashSet());
        initializeCures();
    }

    private void initializeDiseaseStageMap() {
        diseaseStageMap.put(DiseaseStageEn.INCUBATION, createIncubationStage());
        diseaseStageMap.put(DiseaseStageEn.PRODROMAL, createProdromalStage());
        diseaseStageMap.put(DiseaseStageEn.ACUTE, createAcuteStage());
        diseaseStageMap.put(DiseaseStageEn.DECLINING, createDecliningStage());
        diseaseStageMap.put(DiseaseStageEn.CONVALESCENT, createConvalescentStage());
    }

    public String getName() {
        return name;
    }

    public long getImmunityTime() {
        return immunityTime;
    }

    public StageInfo getStageInfo(DiseaseStageEn stage) {
        return diseaseStageMap.get(stage);
    }

    public void applyEffects(LivingEntity entity, DiseaseStageEn stage) {
        /*
        if (entity instanceof Player) {
            ((Player) entity).sendMessage("Applying effects for stage: " + stage.toString());
        }*/
        
        //diseaseStageMap.get(stage).applyEffects(entity);
        
        
        
        switch (stage) {
            case INCUBATION:
                applyIncubationEffects(entity);
                break;
            case PRODROMAL:
                applyProdromalEffects(entity);
                break;
            case ACUTE:
                applyAcuteEffects(entity);
                break;
            case DECLINING:
                applyDecliningEffects(entity);
                break;
            case CONVALESCENT:
                applyConvalescentEffects(entity);
                break;
        }
    }

    public boolean validInfectableType(EntityType type) {
        return infectableTypeMap.containsKey(type);
        //return infectableTypeSet.contains(type);
    }

    void addInfectableType(EntityType type) {
        //infectableTypeSet.add(type);
        if (!infectableTypeMap.containsKey(type)) {
            infectableTypeMap.put(type, new HashSet());
        }
        infectableTypeSet.add(type);
    }

    public boolean validTypeCanInfectType(EntityType infectedType, EntityType type) {

        if (infectableTypeMap.containsKey(infectedType)) {
            if (infectableTypeMap.get(infectedType).contains(type)) {
                return true;
            }
        }
        return false;
    }

    void addTypeCanInfectType(EntityType infectableType, EntityType type) {

        infectableTypeSet.add(infectableType);
        infectableTypeSet.add(type);

        Set<EntityType> canBeInfectedByTypeSet;
        if (!infectableTypeMap.containsKey(infectableType)) {
            canBeInfectedByTypeSet = new HashSet();
        } else {
            canBeInfectedByTypeSet = infectableTypeMap.get(infectableType);
        }
        canBeInfectedByTypeSet.add(type);
        infectableTypeMap.put(infectableType, canBeInfectedByTypeSet);
    }

    public boolean validCure(Cure cure, DiseaseStageEn stage) {
        return cureMap.get(stage).contains(cure);
    }

    void addCure(DiseaseStageEn stage, Cure cure) {
        Set<Cure> cureSet = cureMap.get(stage);
        cureSet.add(cure);
        cureMap.put(stage, cureSet);
    }

    void addVector(EntityType vectorType, EntityType infectedByVectorType, double contactRate, double airborneRate, double airborneDistance) {
        VectorManager.getInstance().addVector(vectorType, infectedByVectorType, contactRate, airborneRate, airborneDistance, this);
    }
    
    void setVectorSpawnChance(EntityType type, double spawnChance) {
        vectorSpawnChanceMap.put(type, spawnChance);
    }
    
    public double getSpawnChance(EntityType type) {
        return vectorSpawnChanceMap.containsKey(type) ? vectorSpawnChanceMap.get(type) : 0;
    }
    

    // Once infected, how can the disease be transmitted?
    abstract void initializeInfectableTypes();

    // How can the disease be cured?
    abstract void initializeCures();

    // How is the disease initially contracted in the world?
    abstract void initializeVectors();

    abstract StageInfo createIncubationStage();

    abstract StageInfo createProdromalStage();

    abstract StageInfo createAcuteStage();

    abstract StageInfo createDecliningStage();

    abstract StageInfo createConvalescentStage();

    abstract void applyIncubationEffects(LivingEntity entity);

    abstract void applyProdromalEffects(LivingEntity entity);

    abstract void applyAcuteEffects(LivingEntity entity);

    abstract void applyDecliningEffects(LivingEntity entity);

    abstract void applyConvalescentEffects(LivingEntity entity);
}
