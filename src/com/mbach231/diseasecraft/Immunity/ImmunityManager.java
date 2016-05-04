package com.mbach231.diseasecraft.Immunity;

import com.mbach231.diseasecraft.DiseaseCraft;
import com.mbach231.diseasecraft.Diseases.Disease;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

public class ImmunityManager {

    Map<LivingEntity, List<ImmunityInfo>> immunityMap;
    List<ImmunityInfo> purgeList;

    public ImmunityManager() {
        immunityMap = new HashMap();
        purgeList = new ArrayList();
    }

    public void addNewImmunity(LivingEntity entity, Disease disease) {
        addNewImmunity(entity, disease, disease.getImmunityTime());
    }

    private void addNewImmunity(LivingEntity entity, Disease disease, long duration) {
        if (duration > 0) {

            List<ImmunityInfo> entityImmunityInfo = immunityMap.get(entity);
            if (entityImmunityInfo == null) {
                entityImmunityInfo = new ArrayList();
            }

            ImmunityInfo existingImmunity = null;
            for (ImmunityInfo immunityInfo : entityImmunityInfo) {
                if (immunityInfo.getDisease().equals(disease)) {
                    existingImmunity = immunityInfo;
                    break;
                }
            }

            // if null, this is new immunity against this disease, add
            if (existingImmunity == null) {
                entityImmunityInfo.add(new ImmunityInfo(disease, duration));
            } // Otherwise check which has higher end immunity time, use that
            else if (existingImmunity.getEndTime() < System.currentTimeMillis() + duration) {
                entityImmunityInfo.remove(existingImmunity);
                entityImmunityInfo.add(new ImmunityInfo(disease, duration));
            }

            immunityMap.put(entity, entityImmunityInfo);
        }
    }

    public Set<LivingEntity> purgeOldImmunities() {
        purgeList.clear();
        
        Set<LivingEntity> effectedEntities = new HashSet();
        
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<LivingEntity, List<ImmunityInfo>> entry : immunityMap.entrySet()) {

            List<ImmunityInfo> immunityInfoList = entry.getValue();

            for (ImmunityInfo immunityInfo : immunityInfoList) {
                DiseaseCraft.log("Checking immunity for entity " + entry.getKey().getType() + " - " + currentTime + " " + immunityInfo.getEndTime());
                if (immunityInfo.immunityExpiried(currentTime)) {
                    purgeList.add(immunityInfo);
                    effectedEntities.add(entry.getKey());
                }
            }
            for (ImmunityInfo purgeImmunity : purgeList) {
                DiseaseCraft.log("Removing immunity - " + purgeImmunity.getDisease().getName());
                immunityInfoList.remove(purgeImmunity);
            }
            immunityMap.put(entry.getKey(), immunityInfoList);

        }
        return effectedEntities;
    }

    public List<ImmunityInfo> getEntityImmunities(LivingEntity entity) {
        return immunityMap.get(entity);
    }

    public boolean entityIsImmune(LivingEntity entity, Disease disease) {
        if (immunityMap.containsKey(entity)) {

            for (ImmunityInfo info : immunityMap.get(entity)) {
                if (info.getDisease().equals(disease)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void removeEntity(LivingEntity entity) {
        immunityMap.remove(entity);
    }

    public void handleConfigLoad(LivingEntity entity, List<ImmunityInfo> infoSet) {
        DiseaseCraft.log("Loading " + infoSet.size() + " immunities for " + entity.getType());
        immunityMap.put(entity, infoSet);
    }
}
