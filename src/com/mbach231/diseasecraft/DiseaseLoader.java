/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mbach231.diseasecraft;

import com.mbach231.diseasecraft.Diseases.Disease;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

public class DiseaseLoader {

    private FileConfiguration config;
    private File dataFolder;
    DiseaseLoader(FileConfiguration config, File dataFolder){
        this.config = config;
        this.dataFolder = dataFolder;
    }
    
    Disease loadDiseases(File diseaseFile) {
        return null;
    }
    
    public void loadDiseases() {
        
        File folder = new File(dataFolder.getPath() + "/Diseases");
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (SecurityException ex) {
                Logger.getLogger(DiseaseCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File customConfigFile = new File(folder, "customConfig.yml");
        if (!customConfigFile.exists()) {
            try {
                customConfigFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DiseaseCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {

            
            config.load(customConfigFile);
            config.set("name", "Default");

            config.save(customConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(DiseaseCraft.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(DiseaseCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
