package com.mbach231.diseasecraft.Diseases;

import java.util.HashMap;
import java.util.Map;

public class DiseaseManager {

    public static enum DiseaseEn {
        COMMON_COLD,
        LYCANTHROPY,
        LYME_DISEASE,
        RABIES,
        SWINE_INFLUENZA,
        VAMPIRISM,
        ZOMBIE_PLAGUE
    }
    private static Map<DiseaseEn, Disease> diseaseMap;

    public DiseaseManager() {
        diseaseMap = new HashMap();
        initializeDiseases();
    }

    private void initializeDiseases() {
        addDisease(DiseaseEn.COMMON_COLD,       new CommonColdDisease());
        addDisease(DiseaseEn.LYCANTHROPY,       new LycanthropyDisease());
        addDisease(DiseaseEn.LYME_DISEASE,      new LymeDisease());
        addDisease(DiseaseEn.RABIES,            new RabiesDisease());
        addDisease(DiseaseEn.SWINE_INFLUENZA,   new SwineInfluenza());
        addDisease(DiseaseEn.VAMPIRISM,         new VampirismDisease());
        addDisease(DiseaseEn.ZOMBIE_PLAGUE,     new ZombiePlagueDisease());
    }

    public static Disease getDisease(DiseaseEn disease) {
        return diseaseMap.get(disease);
    }

    public Disease getDisease(String diseaseStr) {
        Disease disease = null;
        for (DiseaseEn diseaseEn : DiseaseEn.values()) {
            String diseaseEnStr = diseaseEn.toString().toLowerCase();
            if (diseaseStr.toLowerCase().compareTo(diseaseEnStr) == 0) {
                disease = diseaseMap.get(diseaseEn);
                break;
            }
        }

        return disease;
    }

    private void addDisease(DiseaseEn dieaseEn, Disease disease) {
        diseaseMap.put(dieaseEn, disease);
    }
}
