package com.mbach231.diseasecraft.Cures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffectType;

public class CustomCures {

    public static enum CureEn {

        Antibiotic_ADVANCE1,
        Antibiotic_ADVANCE2,
        Antibiotic_CURE,
        Strong_Antibiotic_ADVANCE1,
        Strong_Antibiotic_ADVANCE2,
        Strong_Antibiotic_CURE,
        Weak_Elixir_ADVANCE1,
        Weak_Elixir_ADVANCE2,
        Weak_Elixir_CURE,
        Elixir_ADVANCE1,
        Elixir_ADVANCE2,
        Elixir_CURE,
        Vampire_Draught,
        Vampire_Elixir,
        Werewolf_Elixir
    }
    private static Map<CureEn, Cure> cureMap;
    private static CureFactory cureFactory;

    public CustomCures() {
        cureMap = new HashMap();
        cureFactory = new CureFactory();

        addAntibioticAdvance1();
        addAntibioticAdvance2();
        addAntibioticCure();

        addStrongAntibioticAdvance1();
        addStrongAntibioticAdvance2();
        addStrongAntibioticCure();

        addWeakElixirAdvance1();
        addWeakElixirAdvance2();
        addWeakElixirCure();

        addElixirAdvance1();
        addElixirAdvance2();
        addElixirCure();

        addVampiresDraught();
        addVampiresElixir();
        addWerewolfElixir();
    }

    public static Cure getCure(CureEn cure) {
        return cureMap.get(cure);
    }

    public static Set<Cure> findCure(ItemStack item) {
        Set<Cure> foundCureSet = new HashSet();
        for (Map.Entry<CureEn, Cure> entry : cureMap.entrySet()) {
            if (entry.getValue().isCure(item)) {
                foundCureSet.add(entry.getValue());
            }
        }
        return foundCureSet;
    }

    private void addVampiresDraught() {
        cureFactory.setName("Vampire Draught");
        cureFactory.setMaterial(Material.POTION);
        cureFactory.addToLore("Used to remove Vampirism");
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Vampire_Draught, cure);

        ShapedRecipe recipe = new ShapedRecipe(cure.getItem());
        recipe.shape("gdg", "dmd", "gdg");
        recipe.setIngredient('m', Material.MILK_BUCKET);
        recipe.setIngredient('g', Material.GLOWSTONE_DUST);
        recipe.setIngredient('d', Material.DIAMOND);

        Bukkit.getServer().addRecipe(recipe);
    }

    private void addVampiresElixir() {

        cureFactory.setName("Vampire Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);

        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Vampire_Elixir, cure);

    }

    private void addWerewolfElixir() {

        cureFactory.setName("Werewolf Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);

        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Werewolf_Elixir, cure);

    }

    private void addWeakElixirAdvance1() {
        cureFactory.setName("Weak Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(1);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Weak_Elixir_ADVANCE1, cure);
    }

    private void addWeakElixirAdvance2() {
        cureFactory.setName("Weak Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(2);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Weak_Elixir_ADVANCE2, cure);
    }

    private void addWeakElixirCure() {
        cureFactory.setName("Weak Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Weak_Elixir_CURE, cure);
    }

    private void addElixirAdvance1() {
        cureFactory.setName("Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(1);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Elixir_ADVANCE1, cure);
    }

    private void addElixirAdvance2() {
        cureFactory.setName("Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(2);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Elixir_ADVANCE2, cure);
    }

    private void addElixirCure() {
        cureFactory.setName("Elixir");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Elixir_CURE, cure);
    }

    private void addStrongAntibioticAdvance1() {

        cureFactory.setName("Strong Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(1);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);

        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Strong_Antibiotic_ADVANCE1, cure);

    }

    private void addStrongAntibioticAdvance2() {

        cureFactory.setName("Strong Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(2);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);

        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Strong_Antibiotic_ADVANCE2, cure);

    }

    private void addStrongAntibioticCure() {

        cureFactory.setName("Strong Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);

        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Strong_Antibiotic_CURE, cure);

    }

    private void addAntibioticAdvance1() {
        cureFactory.setName("Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(1);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Antibiotic_ADVANCE1, cure);
    }

    private void addAntibioticAdvance2() {
        cureFactory.setName("Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.advancesStage(2);
        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Antibiotic_ADVANCE2, cure);
    }

    private void addAntibioticCure() {
        cureFactory.setName("Antibiotic");
        cureFactory.setMaterial(Material.POTION);

        cureFactory.fromBrewery();
        cureFactory.addRequiredCustomEffect(PotionEffectType.REGENERATION);
        Cure cure = cureFactory.createCure();
        cureMap.put(CureEn.Antibiotic_CURE, cure);
    }
}
