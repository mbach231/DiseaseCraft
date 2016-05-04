package com.mbach231.diseasecraft;

import com.mbach231.diseasecraft.Infection.InfectionManager;
import com.mbach231.diseasecraft.Diseases.DiseaseManager;
import com.mbach231.diseasecraft.Cures.CustomCures;
import com.mbach231.diseasecraft.Cures.Cure;
import com.mbach231.diseasecraft.Diseases.Disease;
import com.mbach231.diseasecraft.Items.ItemManager;
import com.mbach231.diseasecraft.Vectors.VectorManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class DiseaseCraft extends JavaPlugin implements Listener {

    public static final ChatColor COLOR = ChatColor.WHITE;

    File infectionHistoryFile;
    File vectorFile;

    FileConfiguration infectionHistoryConfig;
    FileConfiguration vectorConfig;

    static FileConfiguration config;

    DiseaseLoader diseaseLoader;

    DiseaseManager diseaseManager;
    InfectionManager infectionManager;
    VectorManager vectorManager;
    CustomCures customCures;

    ItemManager itemManager;

    @Override
    public void onEnable() {
        readConfigs();
        //diseaseLoader = new DiseaseLoader(getConfig(), getDataFolder());
        customCures = new CustomCures();
        vectorManager = VectorManager.getInstance(vectorConfig);
        diseaseManager = new DiseaseManager();

        //loadInfectionManager();
        infectionManager = new InfectionManager(infectionHistoryConfig, vectorConfig);

        itemManager = new ItemManager();

        // diseaseLoader.loadDiseases();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                infectionManager.handleLivingEntities();
            }
        }, 0L, 10 * 20L);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

        long currentTime = System.currentTimeMillis();
        int numDaysBeforePurge = config.getInt("max-days-before-purge");
        long purgeTime = numDaysBeforePurge * 24 * 60 * 60 * 1000;
        long minTime = currentTime - purgeTime;

        if (config.getBoolean("purge-infected-entities")) {
            infectionManager.purgeOldEntries(minTime);
        }
        if (config.getBoolean("purge-vector-entities")) {
            vectorManager.purgeOldVectors(minTime);
        }

        writeConfigs();
        //saveInfectionManager();
    }

    void readConfigs() {
        this.saveDefaultConfig();

        config = this.getConfig();

        infectionHistoryFile = new File(this.getDataFolder(), "infectionHistory.yml");
        infectionHistoryConfig = YamlConfiguration.loadConfiguration(infectionHistoryFile);

        vectorFile = new File(this.getDataFolder(), "vectorHistory.yml");
        vectorConfig = YamlConfiguration.loadConfiguration(vectorFile);

    }

    void writeConfigs() {
        this.saveConfig();
        try {
            infectionHistoryConfig = infectionManager.save();
            infectionHistoryConfig.save(infectionHistoryFile);

            vectorConfig = vectorManager.save();
            vectorConfig.save(vectorFile);
        } catch (IOException ex) {
            Logger.getLogger(DiseaseCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @EventHandler
    public void onInfectedDamageEntity(EntityDamageByEntityEvent event) {
        infectionManager.onEntityDamageEntity(event);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        vectorManager.onCreatureSpawnEvent(event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        infectionManager.handleNewEntity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        infectionManager.handleUnloadEntity(event.getPlayer(), true);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entityInChunk : event.getChunk().getEntities()) {

            if (entityInChunk instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) entityInChunk;
                infectionManager.handleNewEntity(entity);
                vectorManager.handleNewEntity(entity);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entityInChunk : event.getChunk().getEntities()) {

            if (entityInChunk instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) entityInChunk;
                infectionManager.handleUnloadEntity(entity, true);
                vectorManager.handleUnloadEntity(entity);
            }
        }
    }

    /*
     @EventHandler
     public void onPlayerDeath(PlayerDeathEvent event) {

     // Cure any diseases that allow removal on death
     infectionManager.attemptCureOnDeath((LivingEntity) event.getEntity());
     }*/
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        // If player
        if (event.getEntity() instanceof Player) {
            infectionManager.attemptCureOnDeath(event.getEntity());
        } else {
            vectorManager.onEntityDeathEvent(event);
            infectionManager.handleUnloadEntity(event.getEntity(), false);
        }
    }

    // Check for disease detection device interaction
    @EventHandler
    public void onPlayerRightClick(final PlayerInteractEvent event) {

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //event.getPlayer().sendMessage("Not right clicking");

            // If not right clicking correct block, return
            if (event.getClickedBlock().getType() != Material.GLOWING_REDSTONE_ORE
                    && event.getClickedBlock().getType() != Material.REDSTONE_ORE) {
                // event.getPlayer().sendMessage("Block not redstone ore : " + event.getClickedBlock().getType());
                return;
            }

            Location blockLoc = event.getClickedBlock().getLocation();
            Block oneBlockUp = event.getPlayer().getWorld().getBlockAt(blockLoc.add(0, 1, 0));

            // If block above redstone ore isn't a beacon, return
            if (oneBlockUp.getType() != Material.BEACON) {
                return;
            }

            Player player = event.getPlayer();

            // If player is infected
            if (infectionManager.entityInfected(player)) {
                player.getWorld().playSound(player.getLocation(), Sound.CAT_HISS, 2, 0);
                infectionManager.informPlayerOfDiseases(player);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 2, 0);
            }
        }
        
        else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && event.getPlayer().isSneaking()) {
            ItemStack itemInHand = event.getPlayer().getItemInHand();

            if (itemManager.isUnusedDiseaseSensor(itemInHand)) {
                //log("Is unused disease sensor!");

                //log("ENTITY IS INFECTED: " + infectionManager.entityInfected((LivingEntity) event.getRightClicked()));
                Set<String> diseaseStrList = infectionManager.getInfectedEntityDiseaseStringList(event.getPlayer());
                Set<String> vectorStrList = vectorManager.getInfectedEntityDiseaseStringList(event.getPlayer());

                ItemStack updatedSensor = itemManager.updateDiseaseSensor(diseaseStrList, vectorStrList);

                itemInHand.setAmount(itemInHand.getAmount() - 1);

                event.getPlayer().setItemInHand(itemInHand);
                event.getPlayer().getInventory().addItem(updatedSensor);

            }
        } 
    }

    @EventHandler
    public void onPlayerRightClickEntity(PlayerInteractEntityEvent event) {

        if (event.getRightClicked() instanceof LivingEntity) {
            //log("Right clicked entity!");
            ItemStack itemInHand = event.getPlayer().getItemInHand();

            if (itemManager.isUnusedDiseaseSensor(itemInHand)) {
                //log("Is unused disease sensor!");

                //log("ENTITY IS INFECTED: " + infectionManager.entityInfected((LivingEntity) event.getRightClicked()));
                Set<String> diseaseStrList = infectionManager.getInfectedEntityDiseaseStringList((LivingEntity) event.getRightClicked());
                Set<String> vectorStrList = vectorManager.getInfectedEntityDiseaseStringList((LivingEntity) event.getRightClicked());

                ItemStack updatedSensor = itemManager.updateDiseaseSensor(diseaseStrList, vectorStrList);

                itemInHand.setAmount(itemInHand.getAmount() - 1);

                event.getPlayer().setItemInHand(itemInHand);
                event.getPlayer().getInventory().addItem(updatedSensor);

            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("infect")) {
            return onCommandInfect(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("cure")) {
            return onCommandCure(sender, args);
        }

        return true;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack consumed = event.getItem();
        Set<Cure> cureSet = CustomCures.findCure(consumed);

        for (Cure cure : cureSet) {
            infectionManager.applyCure(event.getPlayer(), cure);
        }

    }

    private boolean onCommandInfect(CommandSender sender, String[] args) {

        if (args.length != 2) {
            return false;
        }

        LivingEntity target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(COLOR + "Cannot find entity " + args[0]);
            return true;
        }

        Disease disease = diseaseManager.getDisease(args[1]);
        if (disease == null) {
            sender.sendMessage(COLOR + "Cannot find disease " + args[1]);
            return true;
        }

        //sender.sendMessage(COLOR + "Infecting " + target.getName() + " for " + disease.getTotalDuration() + " seconds");
        //sender.sendMessage("Current time: " + System.currentTimeMillis());
        infectionManager.infectLivingEntity(target, disease);
        return true;
    }

    private boolean onCommandCure(CommandSender sender, String[] args) {

        if (args.length != 2) {
            return false;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(COLOR + "Cannot find entity " + args[0]);
            return true;
        }

        Disease disease = diseaseManager.getDisease(args[1]);
        if (disease == null) {
            sender.sendMessage(COLOR + "Cannot find disease " + args[1]);
            return true;
        }

        sender.sendMessage(COLOR + "Curing " + target.getName() + " of " + disease.getName());
        infectionManager.cureLivingEntityOverride(target, disease);
        return true;
    }

    public static void log(String str) {

        if (config.getBoolean("log-enabled")) {
            Bukkit.getLogger().log(Level.INFO, "[DiseaseCraft] - {0}", str);
        }
    }

}
