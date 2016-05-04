package com.mbach231.diseasecraft.Immunity;

import com.mbach231.diseasecraft.Diseases.Disease;

public class ImmunityInfo {

    private Disease disease;
    private long immunityEndTime;

    ImmunityInfo(Disease disease) {
        this.disease = disease;
        immunityEndTime = System.currentTimeMillis() + disease.getImmunityTime() * 1000;
    }

    ImmunityInfo(Disease disease, long duration) {
        this.disease = disease;
        immunityEndTime = System.currentTimeMillis() + duration * 1000;
    }
    
    public ImmunityInfo(Disease disease, long endTime, boolean b) {
        this.disease = disease;
        immunityEndTime = endTime;
    }
    

    public Disease getDisease() {
        return disease;
    }

    public long getEndTime() {
        return immunityEndTime;
    }

    public boolean immunityExpiried(long time) {
        return time > immunityEndTime;
    }
}
