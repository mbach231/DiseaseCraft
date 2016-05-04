package com.mbach231.diseasecraft.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

    ItemStack diseaseSensorInstance_;

    public ItemManager() {
        addEnchants();
        createDiseaseSensor();
    }

    private void addEnchants() {

    }

    private void createDiseaseSensor() {
        diseaseSensorInstance_ = new ItemStack(Material.STICK, 1);
        ItemMeta meta = diseaseSensorInstance_.getItemMeta();
        List<String> loreList = new ArrayList();

        meta.setDisplayName(ChatColor.WHITE + "Disease Sensor");
        loreList.add("");
        loreList.add(ChatColor.GRAY + "Unused");
        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        diseaseSensorInstance_.setItemMeta(meta);

        diseaseSensorInstance_.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        ShapedRecipe recipe = new ShapedRecipe(diseaseSensorInstance_);
        recipe.shape("rgr", "gsg", "lsl");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('r', Material.REDSTONE);
        recipe.setIngredient('l', Material.INK_SACK, (short) 4);
        recipe.setIngredient('g', Material.GLOWSTONE_DUST);
        Bukkit.getServer().addRecipe(recipe);

    }

    public boolean isUnusedDiseaseSensor(ItemStack item) {

        if (item.getType().equals(Material.STICK)) {

            if (item.getItemMeta().getDisplayName() != null) {
                if (item.getItemMeta().getDisplayName().equals(diseaseSensorInstance_.getItemMeta().getDisplayName())) {
                    
                    if (item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) == 1) {

                        if (item.getItemMeta().getLore() != null) {

                            if (item.getItemMeta().getLore().equals(diseaseSensorInstance_.getItemMeta().getLore())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;

        /*
         return item.getType().equals(Material.STICK)
         && item.getItemMeta().getDisplayName().equals(diseaseSensorInstance_.getItemMeta().getDisplayName())
         && item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) == 1
         && item.getItemMeta().getLore().equals(diseaseSensorInstance_.getItemMeta().getLore());*/
    }

    public ItemStack updateDiseaseSensor(Set<String> diseaseStrSet, Set<String> vectorStrSet) {

        ItemStack newItem;
        newItem = new ItemStack(diseaseSensorInstance_);

        ItemMeta meta = newItem.getItemMeta();

        List<String> lore = new ArrayList();

        lore.add("");
        
        if (diseaseStrSet == null && vectorStrSet == null) {
            lore.add(ChatColor.GRAY + "No diseases found");
        } else {

            lore.add(ChatColor.GRAY + "Diseases found:");

            if (diseaseStrSet != null) {
                lore.addAll(diseaseStrSet);
            }

            if (vectorStrSet != null) {
                lore.addAll(vectorStrSet);
            }
        }

        meta.setLore(lore);
        newItem.setItemMeta(meta);

        return newItem;
    }

}
