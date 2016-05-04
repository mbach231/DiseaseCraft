package com.mbach231.diseasecraft.Infection;

import com.mbach231.diseasecraft.Stages.StageInfo;
import com.mbach231.diseasecraft.Cures.Cure;
import com.mbach231.diseasecraft.Diseases.Disease;
import com.mbach231.diseasecraft.Stages.Stages.DiseaseStageEn;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class InfectedLivingEntity {

    private LivingEntity entity;
    private Disease disease;
    private DiseaseStageEn stage;
    private long infectedTime;
    private long stageTime;
    private long timeLastEffected;

    public InfectedLivingEntity(LivingEntity entity, Disease disease) {

        this.entity = entity;
        this.disease = disease;
        infectedTime = System.currentTimeMillis();
        stageTime = infectedTime;
        timeLastEffected = infectedTime;
        stage = DiseaseStageEn.INCUBATION;
    }
    
    public InfectedLivingEntity(LivingEntity entity, Disease disease, DiseaseStageEn stage, long infectedTime, long stageTime, long timeLastEffected) {

        this.entity = entity;
        this.disease = disease;
        this.infectedTime = infectedTime;
        this.stageTime = stageTime;
        this.timeLastEffected = timeLastEffected;
        this.stage = stage;
    }

    public void updatePlayer(Player player) {
        entity = player;
    }
    
    public LivingEntity getLivingEntity() {
        return entity;
    }

    public Disease getDisease() {
        return disease;
    }

    public long getInfectedTime() {
        return infectedTime;
    }
    
    public long getStageTime() {
        return stageTime;
    }

    public long getTimeLastEffected() {
        return timeLastEffected;
    }

    public DiseaseStageEn getStage() {
        return stage;
    }

    public void attemptToAdvanceStage() {
        long currentTime = System.currentTimeMillis();
        StageInfo info = disease.getStageInfo(stage);

        // Skip this stage
        if (info.skipStage()) {
            advanceToNextStage();
            return;
        }

        long stageDuration = info.getDuration();
        long timeDiff = currentTime - stageTime;

        //entity.sendMessage("Time since last stage change: " + timeDiff);


        // duration == 0 means stage does not advance naturally
        if (stageDuration == 0) {
          //  entity.sendMessage("Stage duration: Infinite");
            return;
        }
        //entity.sendMessage("Stage duration: " + stageDuration * 1000);
        if (timeDiff > stageDuration * 1000) {
            
            // Reset any effects that may have happened to players
            if(entity instanceof Player) {
                Player player = (Player)entity;
                player.setWalkSpeed((float)0.2);
            }
            
            advanceToNextStage();
        }
    }

    public void advanceToNextStage() {
        stageTime = System.currentTimeMillis();
        switch (stage) {
            case INCUBATION:
                stage = DiseaseStageEn.PRODROMAL;
                break;
            case PRODROMAL:
                stage = DiseaseStageEn.ACUTE;
                break;
            case ACUTE:
                stage = DiseaseStageEn.DECLINING;
                break;
            case DECLINING:
                stage = DiseaseStageEn.CONVALESCENT;
                break;
            default:
                stage = DiseaseStageEn.CURED;
        }
    }

    public void applyEffects() {
        long timeSinceLastEffect = System.currentTimeMillis() - timeLastEffected;
        long interval = disease.getStageInfo(stage).getPeriodicInterval();


        // entity.sendMessage("Time since last effect: " + timeSinceLastEffect);
        //entity.sendMessage("Stage interval: " + interval * 1000);

        if (timeSinceLastEffect >= interval * 1000) {
            disease.applyEffects(entity, stage);
            timeLastEffected = System.currentTimeMillis();
        }
    }
    
    
    public boolean validCure(Cure cure) {
        return disease.validCure(cure, stage);
    }
}
