package com.mbach231.diseasecraft.Vectors;

import com.mbach231.diseasecraft.DiseaseCraft;
import com.mbach231.diseasecraft.Diseases.Disease;
import com.mbach231.diseasecraft.Diseases.DiseaseManager;
import com.mbach231.diseasecraft.Diseases.DiseaseManager.DiseaseEn;
import com.mbach231.diseasecraft.Infection.InfectedLivingEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class VectorManager {

    private FileConfiguration config;
    private static VectorManager instance = null;
    private static Map<EntityType, Map<Disease, Set<DiseaseVector>>> diseaseVectorMap;
    //private static Map<LivingEntity, Set<DiseaseVector>> carrierMap;
    private static Map<LivingEntity, Set<Disease>> carrierMap;

    protected VectorManager(FileConfiguration config) {
        this.config = config;
        diseaseVectorMap = new HashMap();
        carrierMap = new ConcurrentHashMap();
    }

    public static VectorManager getInstance(FileConfiguration config) {
        if (instance == null) {
            instance = new VectorManager(config);
        }
        return instance;
    }

    public static VectorManager getInstance() {
        return instance;
    }

    public FileConfiguration save() {

        LivingEntity entity;
        for (Map.Entry<LivingEntity, Set<Disease>> entry : carrierMap.entrySet()) {
            entity = entry.getKey();

            if (!entity.isDead()) {
                saveEntityToConfig(entry.getKey());
            }
        }

        return config;
    }

    public void purgeOldVectors(long minTime) {
        Set<String> uuidStrList = config.getConfigurationSection("vectors").getKeys(false);

        for (String uuidStr : uuidStrList) {

            if (config.contains("vectors." + uuidStr + ".time")) {

                long time = config.getLong("vectors." + uuidStr + ".time");
                if (time > minTime) {
                    continue;
                }
            }

            config.set("vectors." + uuidStr, null);
        }

    }

    public void handleNewEntity(LivingEntity entity) {
        if (config.contains("vectors")) {
            String uuidStr = entity.getUniqueId().toString();

            if (config.contains("vectors." + uuidStr)) {
                List<String> diseaseList = config.getStringList("vectors." + uuidStr + ".list");

                for (String diseaseStr : diseaseList) {
                    DiseaseVector vector = getDiseaseVectorFromString(entity, diseaseStr);

                    if (vector != null) {
                        addDiseaseVector(entity, vector.getDisease());
                        DiseaseCraft.log("Loading vector: " + entity.getType() + ", " + diseaseStr);
                    }
                }
            }
        }

    }

    public void handleUnloadEntity(LivingEntity entity) {
        saveEntityToConfig(entity);
        carrierMap.remove(entity);
    }

    private void saveEntityToConfig(LivingEntity entity) {
        if (carrierMap.containsKey(entity)) {
            String uuidStr = entity.getUniqueId().toString();
            List<String> diseaseStrList = new ArrayList();
            for (Disease disease : carrierMap.get(entity)) {
                diseaseStrList.add(disease.getName().replace(' ', '_'));
            }
            if (!diseaseStrList.isEmpty()) {
                config.set("vectors." + uuidStr + ".time", System.currentTimeMillis());
                config.set("vectors." + uuidStr + ".list", diseaseStrList);
            }
        }
    }

    public String getDiseaseVectorString(DiseaseVector vector) {
        String diseaseStr = vector.getDisease().getName();

        return diseaseStr;
    }

    public DiseaseVector getDiseaseVectorFromString(LivingEntity entity, String string) {
        DiseaseEn diseaseEn = DiseaseEn.valueOf(string.toUpperCase());

        if (diseaseEn != null) {
            Disease disease = DiseaseManager.getDisease(diseaseEn);

            if (disease != null) {
                EntityType type = entity.getType();

                for (DiseaseVector vector : diseaseVectorMap.get(type).get(disease)) {
                    if (vector.getDisease().equals(disease)) {
                        return vector;
                    }
                }

            }
        }

        return null;
    }

    private Set<DiseaseVector> getVectorsByTypeAndDisease(EntityType type, Disease disease) {
        return diseaseVectorMap.get(type).get(disease);
    }

    private void addToVectorMap(EntityType type, DiseaseVector vector) {
        Map<Disease, Set<DiseaseVector>> vectorMap = diseaseVectorMap.get(type);

        if (vectorMap == null) {
            vectorMap = new ConcurrentHashMap();
        }

        Set<DiseaseVector> vectorSet = vectorMap.get(vector.getDisease());

        if (vectorSet == null) {
            vectorSet = new HashSet();
        }

        vectorSet.add(vector);
        vectorMap.put(vector.getDisease(), vectorSet);
        diseaseVectorMap.put(type, vectorMap);
    }

    public void addVector(EntityType vectorType, EntityType infectableByVectorType, double contactRate, double airborneRate, double airborneDistance, Disease disease) {

        addToVectorMap(vectorType, new DiseaseVector(vectorType, infectableByVectorType, contactRate, airborneRate, airborneDistance, disease));
        /*
         Set<DiseaseVector> vectorSet = getVectorsByTypeAndDisease(vectorType, disease);
         if (vectorSet == null) {
         vectorSet = new HashSet();
         }
         vectorSet.add(new DiseaseVector(vectorType, infectableByVectorType, contactRate, spawnRate, airborneRate, airborneDistance, disease));
         diseaseVectorMap.put(vectorType, vectorSet);
         */
    }

    public void addDiseaseVector(LivingEntity entity, Disease disease) {

        if (diseaseVectorMap.get(entity.getType()).containsKey(disease)) {
            Set<Disease> diseaseSet = carrierMap.get(entity);

            if (diseaseSet == null) {
                diseaseSet = new HashSet();
            }

            diseaseSet.add(disease);

            carrierMap.put(entity, diseaseSet);
            DiseaseCraft.log("Added DiseaseVector: " + entity.getType() + ", " + disease.getName());

        } else {
            DiseaseCraft.log("Failed to add Vector, was not valid type for entityType " + entity.getType() + ", " + disease.getName());
        }

    }

    public boolean typeIsVector(EntityType type) {
        return diseaseVectorMap.containsKey(type);
    }

    public Set<DiseaseVector> getDiseaseVectors(EntityType vectorType, EntityType beingInfectedType) {
        if (!diseaseVectorMap.containsKey(vectorType)) {
            return null;
        }
        Set<DiseaseVector> validVectors = null;

        for (Map.Entry<Disease, Set<DiseaseVector>> vectorMapEntry : diseaseVectorMap.get(vectorType).entrySet()) {
            for (DiseaseVector vector : vectorMapEntry.getValue()) {
                if (beingInfectedType == vector.getInfecatbleByVectorType()) {
                    if (validVectors == null) {
                        validVectors = new HashSet();
                    }
                    validVectors.add(vector);
                }
            }
        }

        return validVectors;
    }

    public Set<DiseaseVector> getCarrierDiseaseVectors(LivingEntity entity) {

        Set<DiseaseVector> vectors = null;

        if (carrierMap.containsKey(entity)) {
            for (Disease disease : carrierMap.get(entity)) {

                Set<DiseaseVector> diseaseVector = getVectorsByTypeAndDisease(entity.getType(), disease);

                for (DiseaseVector vector : diseaseVector) {

                    if (vector.getDisease().equals(disease)) {

                        if (vectors == null) {
                            vectors = new HashSet();
                        }
                        vectors.add(vector);
                    }
                }
            }
        }

        return vectors;
    }

    // When a creature spawns, add it to the map if it gains 
    // any DiseaseVectors
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        if (diseaseVectorMap.containsKey(entity.getType()) == false) {
            return;
        }

        Map<Disease, Set<DiseaseVector>> vectorMap = diseaseVectorMap.get(entity.getType());

        for (Map.Entry<Disease, Set<DiseaseVector>> entry : vectorMap.entrySet()) {
            Disease disease = entry.getKey();
            if (Math.random() * 100 <= disease.getSpawnChance(entity.getType())) {

                addDiseaseVector(entity, disease);

            }

        }

        /*
         for (DiseaseVector vector : diseaseVectorMap.get(entity.getType())) {
         if (Math.random() * 100 <= vector.getSpawnAsCarrierRate()) {
         Set<DiseaseVector> carrierDiseaseVectorSet = carrierMap.get(entity);

         if (carrierDiseaseVectorSet == null) {
         carrierDiseaseVectorSet = new HashSet();
         }

         carrierDiseaseVectorSet.add(vector);
         carrierMap.put(entity, carrierDiseaseVectorSet);
         System.out.println("Added carrier: " + entity.getType() + ", " + vector.getDisease().getName());
         }
         }
         */
    }

    // Remove entities from map as they die
    public void onEntityDeathEvent(EntityDeathEvent event) {
        carrierMap.remove(event.getEntity());
        config.set("vectors." + event.getEntity().getUniqueId().toString(), null);
    }

    // Returns a set of new entities to infect
    public Set<InfectedLivingEntity> infectNearbyLivingEntities() {

        //System.out.println("infectNearbyLivingEntities() - " + carrierMap.size() + " carriers!");
        int MAX_DISTANCE = 50;

        Set<InfectedLivingEntity> infectedSet = new HashSet();

        // For all entitys in world
        // for (World world : Bukkit.getWorlds()) {
        //for (LivingEntity infectingEntity : world.getLivingEntities()) {
        for (LivingEntity infectingEntity : carrierMap.keySet()) {

            List<Entity> nearbyEntities = infectingEntity.getNearbyEntities(MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE);
            //System.out.println("infectNearbyLivingEntities() - " + nearbyEntities.size() + " nearby entities!");
            for (Entity entity : nearbyEntities) {
                //System.out.println("infectNearbyLivingEntities() - " + entity.getType() + " type!");
                if (!(entity instanceof LivingEntity)) {
                    continue;
                }

                // Do not compare any entitys that are farther away than any possible disease
                if (infectingEntity.getLocation().distance(entity.getLocation()) > MAX_DISTANCE) {
                    continue;
                }

                for (Disease disease : carrierMap.get(infectingEntity)) {
                    for (DiseaseVector vector : getVectorsByTypeAndDisease(infectingEntity.getType(), disease)) {

                        // Vectors are for specific entity -> entity transmission, if
                        // this vector is not for this pair, do not apply
                        if (vector.getInfecatbleByVectorType() != entity.getType()) {
                            continue;
                        }

                        // If this type cannot be infected by this disease, do not infect 
                        if (!vector.getDisease().validInfectableType(entity.getType())) {
                            continue;
                        }

                        // If 0's, not contagious
                        if (vector.getAirborneTransmissionDistance() == 0 || vector.getAirborneTransmissionRate() == 0) {
                            continue;
                        }

                        // If fail random chance, do not infect
                        if (Math.random() * 100 > vector.getAirborneTransmissionRate()) {
                            continue;
                        }

                        // If out of range, do not infect
                        if (infectingEntity.getLocation().distance(entity.getLocation()) > vector.getAirborneTransmissionDistance()) {
                            continue;
                        }
                        // Bukkit.broadcastMessage(infectingEntity.getType() + " infecting " + entity.getType() + " with " + vector.getDisease().getName() + "!");
                        infectedSet.add(new InfectedLivingEntity((LivingEntity) entity, vector.getDisease()));

                    }
                    //}
                }
            }
        }
        //}
        return infectedSet;
    }

    public Set<String> getInfectedEntityDiseaseStringList(LivingEntity entity) {
        Set<DiseaseVector> vectorSet = getCarrierDiseaseVectors(entity);

        if (vectorSet != null) {

            if (!vectorSet.isEmpty()) {

                Set<String> diseaseStrSet = new HashSet();

                for (DiseaseVector vector : vectorSet) {

                    diseaseStrSet.add(vector.getDisease().getName());

                }

                return diseaseStrSet;
            }

        }

        return null;
    }
}
