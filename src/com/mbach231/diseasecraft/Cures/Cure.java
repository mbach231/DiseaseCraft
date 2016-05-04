package com.mbach231.diseasecraft.Cures;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import org.bukkit.potion.PotionEffectType;

public class Cure {

    ItemStack item;
    boolean advancesStage;
    int numStages;
    boolean fromBrewery;

    Cure(ItemStack item) {
        this.item = item;
        advancesStage = false;
        numStages = 0;
        fromBrewery = false;
    }

    Cure(ItemStack item, boolean advancesStage, int numStages) {
        this.item = item;
        this.advancesStage = advancesStage;
        this.numStages = numStages;
        fromBrewery = false;
    }

    Cure(ItemStack item, boolean advancesStage, int numStages, boolean fromBrewery) {
        this.item = item;
        this.advancesStage = advancesStage;
        this.numStages = numStages;
        this.fromBrewery = fromBrewery;
    }

    public boolean isCure(ItemStack item) {
        
        // If Brewery Item, need to make some manual checks
        if(fromBrewery) {
            
            if(item.getType() != Material.POTION) {
                return false;
            }

            PotionMeta meta = (PotionMeta)item.getItemMeta();
            
            // If item has been repaired, might be fake, return false
            if (meta.toString().contains("repair-cost")) {
                    return false;
                }
            
            String name1 = ChatColor.stripColor(this.item.getItemMeta().getDisplayName());
            String name2 = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            
            // If names aren't equal, not the cure    
            if(!name1.equalsIgnoreCase(name2)) {
                return false;
            }
            
            // If item doesn't have Regen, might not be Brewery item
            if(!meta.hasCustomEffect(PotionEffectType.REGENERATION)) {
                return false;
            }
            
            return true;
                  
        }
        
        return this.item.isSimilar(item);
    }
    
    public boolean fullyCures() {
        return !advancesStage;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public int getNumStagesAdvanced() {
        return numStages;
    }
}
