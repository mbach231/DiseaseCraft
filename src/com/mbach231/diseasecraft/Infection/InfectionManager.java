package com.mbach231.diseasecraft.Infection;

import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Cures.Cure;
import com.mbach231.diseasecraft.DiseaseCraft;
import com.mbach231.diseasecraft.Diseases.Disease;
import com.mbach231.diseasecraft.Diseases.DiseaseManager;
import com.mbach231.diseasecraft.Diseases.DiseaseManager.DiseaseEn;
import com.mbach231.diseasecraft.Immunity.ImmunityInfo;
import com.mbach231.diseasecraft.Immunity.ImmunityManager;
import com.mbach231.diseasecraft.Stages.Stages;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import com.mbach231.diseasecraft.Vectors.DiseaseVector;
import com.mbach231.diseasecraft.Vectors.VectorManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class InfectionManager {

    private FileConfiguration config;

    private final Map<LivingEntity, Set<InfectedLivingEntity>> infectedLivingEntityMap;
    private final VectorManager vectorManager;
    private final ImmunityManager immunityManager;

    private final Set<InfectedLivingEntity> removeInfectionSet;
    private final Map<LivingEntity, Set<Disease>> infectionMap;
    private final Set<InfectedLivingEntity> toCureSet;

    public InfectionManager(FileConfiguration infectionConfig, FileConfiguration vectorConfig) {
        this.config = infectionConfig;

        vectorManager = VectorManager.getInstance(vectorConfig);
        immunityManager = new ImmunityManager();
        infectionMap = new HashMap();

        removeInfectionSet = new HashSet();
        toCureSet = new HashSet();

        infectedLivingEntityMap = new HashMap();

        //loadConfig();
    }

    public void handleLivingEntities() {

        adjustDiseaseStages();
        removeCuredLivingEntities();
        removeImmunities();
        infectNearbyLivingEntities();
        infectLivingEntitiesByVectors();
        addEffectsToLivingEntities();

        //printInfectedInfo();
    }

    /*
     public final void loadConfig() {
     long prevShutdownTime = config.getLong("shutdown-time");
     long currentTime = System.currentTimeMillis();
     long shutdownTimeDiff = currentTime - prevShutdownTime;
     long timeDifference;

     if (config.contains("infected-history")) {
     Set<String> uuidStrSet = config.getConfigurationSection("infected-history").getKeys(false);
     if (uuidStrSet != null) {
     DiseaseCraft.log("Num found UUIDs: " + uuidStrSet.size());
     String uuidStr;
     List<String> stringSet;
     InfectedLivingEntity infected;

     for (World world : Bukkit.getWorlds()) {
     for (LivingEntity entity : world.getLivingEntities()) {
     uuidStr = entity.getUniqueId().toString();
     if (uuidStrSet.contains(uuidStr)) {

     long time = config.getLong("infected-history." + uuidStr + ".time");
     timeDifference = currentTime - time;
     stringSet = (List<String>) config.getList("infected-history." + uuidStr + ".info");

     for (String historyStr : stringSet) {
     infected = getInfectedEntityFromString(entity, historyStr, timeDifference);
     if (infected != null) {
     DiseaseCraft.log("Loading infected entity!");
     addInfectedLivingEntity(infected);
     }
     }

     stringSet = (List<String>) config.getList("infected-history." + uuidStr + ".immune");

     if (!stringSet.isEmpty()) {
     ImmunityInfo info;
     List<ImmunityInfo> infoSet = new ArrayList();
     for (String immunityStr : stringSet) {
     info = getImmunityInfoFromString(immunityStr);
     if (info != null) {
     infoSet.add(info);
     }
     }
     if (!infoSet.isEmpty()) {
     immunityManager.handleConfigLoad(entity, infoSet);
     }
     }

     // Remove UUIDs as they're used
     uuidStrSet.remove(uuidStr);
     }
     }
     }

     DiseaseCraft.log("Num invalid UUIDs: " + uuidStrSet.size());
     // All valid UUIDs removed from set. Must handle invalid UUIDs

     // Check for UUIDs of offline players, leave those untouched. Will need to load those on login
     OfflinePlayer offlinePlayer;
     UUID uuid;
     for (String uuidStr2 : uuidStrSet) {
     uuid = UUID.fromString(uuidStr2);

     if (uuid != null) {
     offlinePlayer = Bukkit.getOfflinePlayer(uuid);

     if (offlinePlayer != null) {
     if (offlinePlayer.hasPlayedBefore()) {
     DiseaseCraft.log("Ignoring offline player - " + offlinePlayer.getName());
     continue;
     }
     }
     }

     //config.set("infected-history." + uuidStr2, null);
     }
     } else {
     DiseaseCraft.log("No UUIDs found, set was null");
     }
     } else {
     DiseaseCraft.log("No infected history was found");
     }
     }*/
    public FileConfiguration save() {
        long currentTime = System.currentTimeMillis();
        //config.set("shutdown-time", currentTime);

        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            // Set to 0 on save instead of logging shutdown time repeatedly
            setEntityHistory(entry.getKey(), currentTime);
        }

        return config;
    }

    public void purgeOldEntries(long minTime) {
        if (config.contains("infected-history")) {
            Set<String> uuidStrSet = config.getConfigurationSection("infected-history").getKeys(false);

            if (uuidStrSet != null) {
                OfflinePlayer offlinePlayer;
                UUID uuid;
                for (String uuidStr : uuidStrSet) {
                    if ((config.contains("infected-history." + uuidStr + ".info")
                            || config.contains("infected-history." + uuidStr + ".immune"))
                            && config.contains("infected-history." + uuidStr + ".time")) {

                        // Check if player, do never purge player infections
                        uuid = UUID.fromString(uuidStr);

                        if (uuid != null) {
                            offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                            if (offlinePlayer != null) {
                                if (offlinePlayer.hasPlayedBefore()) {
                                    continue;
                                }
                            }
                        }

                        long time = config.getLong("infected-history." + uuidStr + ".time");

                        if (time < minTime) {
                            config.set("infected-history." + uuidStr, null);
                        }
                    } else {
                        config.set("infected-history." + uuidStr, null);
                    }

                }
            }
        }
    }

    private void setEntityHistory(LivingEntity entity, long time) {

        String uuidStr = entity.getUniqueId().toString();

        config.set("infected-history." + uuidStr, null);

        if (infectedLivingEntityMap.containsKey(entity)) {
            List<String> infectedInfoStrList = new ArrayList();
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(entity)) {

                String saveStr = getInfectedEntityString(infected);
                infectedInfoStrList.add(saveStr);
            }

            if (!infectedInfoStrList.isEmpty()) {
                config.set("infected-history." + uuidStr + ".time", time);
                config.set("infected-history." + uuidStr + ".info", infectedInfoStrList);
            }
        }

        List<ImmunityInfo> immunityInfoSet;
        long currentTime = System.currentTimeMillis();
        if ((immunityInfoSet = immunityManager.getEntityImmunities(entity)) != null) {
            List<String> immunityInfoStrList = new ArrayList();
            for (ImmunityInfo info : immunityInfoSet) {

                // If immunity about to expire on next heartbeat, remove immunity
                if (info.getEndTime() > currentTime) {
                    String infoStr = getImmunityInfoString(info);
                    immunityInfoStrList.add(infoStr);
                }
            }
            if (!immunityInfoSet.isEmpty()) {
                config.set("infected-history." + uuidStr + ".time", time);
                config.set("infected-history." + uuidStr + ".immune", immunityInfoStrList);
            }
        }

    }

    public void handleNewEntity(LivingEntity entity) {

        // Need to go through infectedLivingEntityMap by UUID 
        // If player not in map, attempt to load history from config
        if (!infectedLivingEntityMap.containsKey(entity)) {
            String uuidStr = entity.getUniqueId().toString();
            if (config.contains("infected-history." + uuidStr)) {
                long lastTimeOnline = config.getLong("infected-history." + uuidStr + ".time");
                long timeDiff = System.currentTimeMillis() - lastTimeOnline;
                List<String> stringList = (List<String>) config.getList("infected-history." + uuidStr + ".info");
                InfectedLivingEntity infected;

                if (stringList != null) {
                    Set<Disease> diseaseSet = new HashSet();
                    for (String historyStr : stringList) {
                        infected = getInfectedEntityFromString(entity, historyStr, timeDiff);
                        if (infected != null) {
                            DiseaseCraft.log("Loading infectetion for Entity - " + entity.getType() + ", " + infected.getDisease().getName());

                            // If Disease already added, do not add again
                            if (!diseaseSet.contains(infected.getDisease())) {
                                addInfectedLivingEntity(infected);
                                diseaseSet.add(infected.getDisease());
                            } else {
                                DiseaseCraft.log("Warning - Found multiple instances of " + infected.getDisease().getName());
                            }

                        }
                    }
                    // If diseaseSet not empty, valid infections found, update infection map
                    if (!diseaseSet.isEmpty()) {
                        infectionMap.put(entity, diseaseSet);
                    }
                }

                stringList = (List<String>) config.getList("infected-history." + uuidStr + ".immune");

                if (stringList != null) {
                    ImmunityInfo info;
                    List<ImmunityInfo> infoSet = new ArrayList();
                    for (String immunityStr : stringList) {
                        info = getImmunityInfoFromString(immunityStr);
                        if (info != null) {
                            DiseaseCraft.log("Loading immunity - " + entity.getType() + ", " + info.getDisease().getName());
                            infoSet.add(info);
                        }
                    }
                    if (!infoSet.isEmpty()) {
                        immunityManager.handleConfigLoad(entity, infoSet);
                    }

                }
            }

        }
    }

    public void handleUnloadEntity(LivingEntity entity, boolean saveHistory) {

        if (saveHistory) {
            setEntityHistory(entity, System.currentTimeMillis());
        } else {
            config.set("infected-history." + entity.getUniqueId().toString(), null);
        }

        if (infectedLivingEntityMap.containsKey(entity)) {
            DiseaseCraft.log("Removing infected entity - " + entity.getType());
        }
        infectedLivingEntityMap.remove(entity);
        infectionMap.remove(entity);
        immunityManager.removeEntity(entity);
    }

    private String getInfectedEntityString(InfectedLivingEntity infected) {
        String diseaseStr = infected.getDisease().getName().replace(' ', '_');
        String stageStr = infected.getStage().name();
        String infectedTimeStr = String.valueOf(infected.getInfectedTime());
        String stageTimeStr = String.valueOf(infected.getStageTime());
        String timeLastEffectedStr = String.valueOf(infected.getTimeLastEffected());

        String saveStr = diseaseStr + " " + stageStr + " " + infectedTimeStr + " " + stageTimeStr + " " + timeLastEffectedStr;
        return saveStr;
    }

    private InfectedLivingEntity getInfectedEntityFromString(LivingEntity entity, String string, long timeDiff) {
        String[] infectedStrInfo = string.split(" ");

        if (infectedStrInfo.length != 5) {
            return null;
        }

        Disease disease;
        DiseaseStageEn stage;
        long infectedTime;
        long stageTime;
        long timeLastEffected;

        disease = DiseaseManager.getDisease(DiseaseEn.valueOf(infectedStrInfo[0].toUpperCase()));

        if (disease == null) {
            return null;
        }

        stage = Stages.DiseaseStageEn.valueOf(infectedStrInfo[1]);

        if (stage == null) {
            return null;
        }
        try {
            infectedTime = Long.parseLong(infectedStrInfo[2]) + timeDiff;
            stageTime = Long.parseLong(infectedStrInfo[3]) + timeDiff;
            timeLastEffected = Long.parseLong(infectedStrInfo[4]) + timeDiff;
        } catch (NumberFormatException e) {
            return null;
        }

        return new InfectedLivingEntity(entity, disease, stage, infectedTime, stageTime, timeLastEffected);

    }

    private String getImmunityInfoString(ImmunityInfo info) {
        String diseaseStr = info.getDisease().getName().replace(' ', '_');
        String timeRemaining = String.valueOf(info.getEndTime() - System.currentTimeMillis());

        String saveStr = diseaseStr + " " + timeRemaining;
        return saveStr;
    }

    private ImmunityInfo getImmunityInfoFromString(String string) {
        String[] infoStr = string.split(" ");

        if (infoStr.length == 2) {
            Disease disease;
            long endTime;
            disease = DiseaseManager.getDisease(DiseaseEn.valueOf(infoStr[0].toUpperCase()));

            try {
                endTime = Long.parseLong(infoStr[1]) + System.currentTimeMillis();
                // If values valid, return Info, otherwise null
                return (disease != null) && (endTime > 0) ? new ImmunityInfo(disease, endTime, true) : null;
            } catch (NumberFormatException e) {
            }

        }

        return null;
    }

    // Handle infected attacking another living entity
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity damagedEntity = (LivingEntity) event.getEntity();
        LivingEntity attackingEntity = (LivingEntity) event.getDamager();

        // If Player, check to make sure no item in hand, can only spread
        // diseases via their fists
        if (attackingEntity instanceof Player) {

            if (attackingEntity.getEquipment().getItemInHand() != null) {
                return;
            }
        }

        if (entityInfected(attackingEntity)) {

            handleInfectedAttackingEntity(attackingEntity, damagedEntity);
        } // END IF entity infected
        else if (vectorManager.typeIsVector(attackingEntity.getType())) {
            handleVectorAttackingEntity(attackingEntity, damagedEntity);
        }
    }

    private void handleInfectedAttackingEntity(LivingEntity attacker, LivingEntity victim) {

        if (infectedLivingEntityMap.containsKey(attacker)) {
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(attacker)) {
                if (attacker.equals(infected.getLivingEntity())) {
                    Disease disease = infected.getDisease();

                    if (disease.validTypeCanInfectType(attacker.getType(), victim.getType())) {
                        StageInfo stageInfo = disease.getStageInfo(infected.getStage());

                        if (Math.random() * 100 <= stageInfo.getContactTransmissionRate()) {
                            infectLivingEntity(victim, disease);
                        }
                    }
                }
            }
        }
    }

    private void handleVectorAttackingEntity(LivingEntity attacker, LivingEntity victim) {
        //Set<DiseaseVector> vectors = vectorManager.getDiseaseVector(attacker.getType(), victim.getType());

        Set<DiseaseVector> vectors = vectorManager.getCarrierDiseaseVectors(attacker);

        // If vector is null, no valid vector found
        if (vectors == null) {
            return;
        }

        for (DiseaseVector vector : vectors) {

            if (Math.random() * 100 <= vector.getContactTransmissionRate()) {
                infectLivingEntity(victim, vector.getDisease());
            }
        }
    }

    // Should only be used by console
    public void infectLivingEntity(LivingEntity entity, Disease disease) {

        // If not infectable type, entity cannot be infected
        if (!disease.validInfectableType(entity.getType())) {
            return;
        }

        // Cannot infect if already infected
        if (entityHasDisease(entity, disease)) {
            return;
        }
        InfectedLivingEntity infected;
        if (entity instanceof Player) {
            infected = new InfectedLivingEntity((Player) entity, disease);
        } else {
            infected = new InfectedLivingEntity(entity, disease);
        }
        //infectedLivingEntitySet.add(infected);
        addInfectedLivingEntity(infected);

        /*
         if (entity instanceof Player) {
         Player player = (Player) entity;
         player.sendMessage(DiseaseCraft.COLOR + "You have become infected with " + disease.getName() + "!");
         }*/
        //entity.sendMessage(DiseaseCraft.COLOR + "You have become infected with " + disease.getName() + "!");
        Set<Disease> entityDiseases;
        if ((entityDiseases = infectionMap.get(entity)) == null) {
            entityDiseases = new HashSet();
        }
        entityDiseases.add(disease);
        infectionMap.put(entity, entityDiseases);
    }

    public void attemptCureOnDeath(LivingEntity entity) {

        if (infectedLivingEntityMap.containsKey(entity)) {
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(entity)) {
                if (infected.getLivingEntity() instanceof Player) {

                    if (infected.getDisease().getStageInfo(infected.getStage()).getDeathCures()) {
                        toCureSet.add(infected);
                    }
                }
            }
            for (InfectedLivingEntity infected : toCureSet) {
                cureLivingEntity(infected, true);
            }
            toCureSet.clear();
        }

    }

    private void infectLivingEntity(InfectedLivingEntity infected) {
        // Cannot infect if already infected
        if (entityHasDisease(infected.getLivingEntity(), infected.getDisease())) {
            return;
        }
        /*
         // Last check if not infectable type, entity cannot be infected
         if (!infected.getDisease().validInfectableType(infected.getLivingEntity().getType())) {
         return;
         }*/

        // Cannot infect if immune
        if (immunityManager.entityIsImmune(infected.getLivingEntity(), infected.getDisease())) {
            return;
        }

        LivingEntity entity = infected.getLivingEntity();
        Disease disease = infected.getDisease();

        //infectedLivingEntitySet.add(infected);
        addInfectedLivingEntity(infected);

        //entity.sendMessage(DiseaseCraft.COLOR + "You have become infected with " + disease.getName() + "!");
        Set<Disease> entityDiseases;
        if ((entityDiseases = infectionMap.get(entity)) == null) {
            entityDiseases = new HashSet();
        }

        entityDiseases.add(disease);
        infectionMap.put(entity, entityDiseases);
    }

    public boolean entityInfected(LivingEntity entity) {

        if (infectionMap.get(entity) == null) {
            return false;
        }

        return !infectionMap.get(entity).isEmpty();
    }

    public void informPlayerOfDiseases(Player player) {
        if (infectionMap.get(player) == null) {
            return;
        }
        if (infectionMap.get(player).isEmpty()) {
            return;
        }
        player.sendMessage("You are infected with:");

        for (Disease disease : infectionMap.get(player)) {
            player.sendMessage("-" + disease.getName());
        }

    }

    private boolean isOfflinePlayer(LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            return !player.isOnline();
        }
        return false;
    }

    public void applyCure(LivingEntity entity, Cure cure) {

        if (infectedLivingEntityMap.containsKey(entity)) {
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(entity)) {

                if (infected.getLivingEntity().equals(entity)) {

                    if (infected.validCure(cure)) {

                        if (cure.fullyCures()) {
                            cureLivingEntity(infected, true);
                        } else {

                            for (int i = 0; i < cure.getNumStagesAdvanced(); i++) {
                                infected.advanceToNextStage();
                            }
                        }
                    }
                }
            }
        }

    }

    boolean entityHasDisease(LivingEntity entity, Disease disease) {

        if (infectedLivingEntityMap.containsKey(entity)) {
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(entity)) {
                if (infected.getDisease().equals(disease)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cureLivingEntityOverride(LivingEntity entity, Disease disease) {

        if (infectedLivingEntityMap.containsKey(entity)) {
            InfectedLivingEntity cureInfected = null;
            for (InfectedLivingEntity infected : infectedLivingEntityMap.get(entity)) {
                if (infected.getLivingEntity().equals(entity) && infected.getDisease().equals(disease)) {
                    cureInfected = infected;
                    break;
                }
            }
            if (cureInfected != null) {
                cureLivingEntity(cureInfected, false);
            }
        }
    }

    public void cureLivingEntity(InfectedLivingEntity infected, boolean immunize) {

        //infectedLivingEntitySet.remove(infected);
        removeInfectedLivingEntity(infected);

        if (immunize) {
            immunityManager.addNewImmunity(infected.getLivingEntity(), infected.getDisease());
        }

        /*
         if (infected.getLivingEntity() instanceof Player) {
         ((Player) infected.getLivingEntity()).sendMessage(DiseaseCraft.COLOR + "You no longer have " + infected.getDisease().getName() + "!");

         }
         */
        //infected.getLivingEntity().sendMessage(DiseaseCraft.COLOR + "You no longer have " + infected.getDisease().getName() + "!");
        Set<Disease> entityDiseases = infectionMap.get(infected.getLivingEntity());
        entityDiseases.remove(infected.getDisease());
        infectionMap.put(infected.getLivingEntity(), entityDiseases);

        // Reset any possible changes to player
        if (infected.getLivingEntity() instanceof Player) {
            Player player = (Player) infected.getLivingEntity();
            player.setWalkSpeed((float) 0.2);
        }

    }

    private void printInfectedInfo() {
        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            for (InfectedLivingEntity infected : entry.getValue()) {
                String str = infected.getLivingEntity().getType().toString() + " infected with " + infected.getDisease().getName() + "\n";
                str += "Stage: " + infected.getStage().toString() + "\n";

                if (infected.getLivingEntity() instanceof Player) {
                    Player player = (Player) infected.getLivingEntity();
                    Bukkit.broadcastMessage(player.getDisplayName());
                }

                Bukkit.broadcastMessage(str);

                if (infected.getLivingEntity() instanceof Player) {
                    ((Player) infected.getLivingEntity()).sendMessage("You're infected!");
                }

            }
        }

    }

    private void adjustDiseaseStages() {
        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            LivingEntity entity = entry.getKey();
            if (!isOfflinePlayer(entity)) {
                for (InfectedLivingEntity infected : entry.getValue()) {

                    infected.attemptToAdvanceStage();
                }
            }
        }

    }

    private void removeCuredLivingEntities() {

        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            LivingEntity entity = entry.getKey();
            if (!isOfflinePlayer(entity)) {
                for (InfectedLivingEntity infected : entry.getValue()) {

                    if (infected.getStage() == DiseaseStageEn.CURED) {
                        removeInfectionSet.add(infected);
                    }
                }
            }
        }

        for (InfectedLivingEntity infected : removeInfectionSet) {
            //infected.getLivingEntity().sendMessage("You've been cured of " + infected.getDisease().getName() + "!");

            cureLivingEntity(infected, true);
            //infectedLivingEntitySet.remove(infected);
        }
        removeInfectionSet.clear();
    }

    private void addEffectsToLivingEntities() {
        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            LivingEntity entity = entry.getKey();
            if (!isOfflinePlayer(entity)) {
                for (InfectedLivingEntity infected : entry.getValue()) {

                    infected.applyEffects();
                }
            }
        }
    }

    private void infectLivingEntitiesByVectors() {
        for (InfectedLivingEntity infected : vectorManager.infectNearbyLivingEntities()) {
            infectLivingEntity(infected);
        }
    }

    private void infectNearbyLivingEntities() {

        Set<InfectedLivingEntity> newlyInfectedSet = new HashSet();
        for (Map.Entry<LivingEntity, Set<InfectedLivingEntity>> entry : infectedLivingEntityMap.entrySet()) {
            if (!isOfflinePlayer(entry.getKey())) {
                for (InfectedLivingEntity infected : entry.getValue()) {
                    StageInfo stageInfo = infected.getDisease().getStageInfo(infected.getStage());

                    // If 0, entity is not contagious
                    if (stageInfo.getTransmissionDistance() == 0 || stageInfo.getAirborneTransmissionRate() == 0) {
                        continue;
                    }

                    int MAX_DISTANCE = 25;

                    List<Entity> nearbyEntities = infected.getLivingEntity().getNearbyEntities(MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE);
                    for (Entity entity : nearbyEntities) {

                        if (!(entity instanceof LivingEntity)) {
                            return;
                        }

                        LivingEntity nearbyEntity = (LivingEntity) entity;
            //for (LivingEntity nearbyEntity : infected.getLivingEntity().getWorld().getLivingEntities()) {

                        // If not infectable type, entity cannot be infected
                        if (!infected.getDisease().validTypeCanInfectType(infected.getLivingEntity().getType(), nearbyEntity.getType())) {
                            continue;
                        }

                        // Cannot infect self
                        if (nearbyEntity.equals(infected.getLivingEntity())) {
                            continue;
                        }

                        // If failed random chance, entity cannot be infected
                        if (Math.random() * 100 > stageInfo.getAirborneTransmissionRate()) {
                            continue;
                        }

                        // If entity not within range, entity cannot be infected
                        if (infected.getLivingEntity().getLocation().distance(nearbyEntity.getLocation()) > stageInfo.getTransmissionDistance()) {
                            continue;
                        }
                        newlyInfectedSet.add(new InfectedLivingEntity(nearbyEntity, infected.getDisease()));

                        //infectLivingEntity(nearbyEntity, infected.getDisease());
                    }
                }
            }
        }

        for (InfectedLivingEntity newlyInfected : newlyInfectedSet) {
            infectLivingEntity(newlyInfected);
        }
    }

    // Removes immunities from immunityManager, updates config
    private void removeImmunities() {
        long currentTime = System.currentTimeMillis();
        Set<LivingEntity> entitySet = immunityManager.purgeOldImmunities();
        for (LivingEntity entity : entitySet) {

            String uuidStr = entity.getUniqueId().toString();

            List<ImmunityInfo> immunityInfoList;
            if ((immunityInfoList = immunityManager.getEntityImmunities(entity)) != null) {
                List<String> immunityInfoStrList = new ArrayList();
                for (ImmunityInfo info : immunityInfoList) {

                    // If immunity about to expire on next heartbeat, remove immunity
                    if (info.getEndTime() > currentTime) {
                        String infoStr = getImmunityInfoString(info);
                        immunityInfoStrList.add(infoStr);
                    }
                }
                if (!immunityInfoList.isEmpty()) {
                    config.set("infected-history." + uuidStr + ".time", currentTime);
                    config.set("infected-history." + uuidStr + ".immune", immunityInfoStrList);
                } else {
                    config.set("infected-history." + uuidStr + ".time", null);
                    config.set("infected-history." + uuidStr + ".immune", null);
                }
            } else {
                config.set("infected-history." + uuidStr + ".immune", null);
            }
        }

    }

    private void addInfectedLivingEntity(InfectedLivingEntity infected) {

        LivingEntity entity = infected.getLivingEntity();
        if (!infectedLivingEntityMap.containsKey(entity)) {
            infectedLivingEntityMap.put(entity, new HashSet());
        }
        Set<InfectedLivingEntity> infectedSet = infectedLivingEntityMap.get(entity);
        infectedSet.add(infected);
    }

    private void removeInfectedLivingEntity(InfectedLivingEntity infected) {
        LivingEntity entity = infected.getLivingEntity();

        Set<InfectedLivingEntity> infectedSet = infectedLivingEntityMap.get(entity);
        if (infectedSet != null) {
            infectedSet.remove(infected);
            infectedLivingEntityMap.put(entity, infectedSet);
        }
    }

    public Set<String> getInfectedEntityDiseaseStringList(LivingEntity entity) {

        if (infectedLivingEntityMap.containsKey(entity)) {
            Set<InfectedLivingEntity> infectedSet = infectedLivingEntityMap.get(entity);

            if (!infectedSet.isEmpty()) {
                Set<String> diseaseStrSet = new HashSet();

                for (InfectedLivingEntity infected : infectedSet) {

                    diseaseStrSet.add(infected.getDisease().getName());

                }

                return diseaseStrSet;
            }
        }

        return null;
    }

}
