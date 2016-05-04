package com.mbach231.diseasecraft.Cures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CureFactory {

    private String name = "";
    private Material material = null;
    private List<String> lore = null;
    private boolean advancesStage = false;
    int numStages = 0;
    
    private boolean fromBrewery = false;
    private Set<PotionEffect> requiredEffects = null;
    
    private void resetFactory() {
        name = "";
        material = null;
        lore = null;
        advancesStage = false;
        numStages = 0;

        requiredEffects = null;
    }

    public void setName(String str) {
        name = str;
    }

    public void setMaterial(Material mat) {
        material = mat;
    }

    public void setLore(List<String> strs) {
        lore = strs;
    }

    public void advancesStage(int numStages) {
        advancesStage = true;
        this.numStages = numStages;
    }
    
    
    public void addToLore(String str) {
        if (lore == null) {
            lore = new ArrayList();
        }
        lore.add(str);
    }
  
    public void fromBrewery() {
        fromBrewery = true;
    }
    
    public void addRequiredCustomEffect(PotionEffectType type) {
        if(requiredEffects == null) {
            requiredEffects = new HashSet();
        }
        requiredEffects.add(new PotionEffect(type, 0, 0));
    }
    
    /*
    public void setPotionMeta(PotionMeta meta) {
        this.meta = meta;
    }
*/
    private ItemMeta adjustMeta(ItemMeta meta, String newName, List<String> lore) {
        meta.setDisplayName(newName);
        if (lore != null) {
            meta.setLore(lore);
        }
        return meta;
    }

    public Cure createCure() {
        /*
         if (material == null) {
         return null;
         }
         */
        ItemStack item = new ItemStack(material);
        if (material == Material.POTION && requiredEffects != null) {
            PotionMeta meta = (PotionMeta)item.getItemMeta();
            
            for(PotionEffect effect : requiredEffects) {
                meta.addCustomEffect(effect, true);
            }
            
            item.setItemMeta(meta);
        }
        
        item.setItemMeta(adjustMeta(item.getItemMeta(), name, lore));

        Cure cure = new Cure(item, advancesStage, numStages, fromBrewery);
        resetFactory();
        return cure;
    }
}
