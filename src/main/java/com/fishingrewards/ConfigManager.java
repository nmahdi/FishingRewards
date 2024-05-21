package com.fishingrewards;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigManager {

    public int CURRENT_VERSION = 1;

    private final FishingRewards plugin;
    private final PluginLogger logger;

    private final File configFile;
    private final File rewardsFile;
    private RewardConfiguration rewardsConfig;
    private YamlConfiguration config;

    private boolean worldGuardEnabled = false;

    private int configVersion = 1;
    private int itemClearTime = 60;
    private double luckIncrease = 2;
    private double luckDecrease = 2;
    private boolean allowLuckAttribute = true;
    private Luck offhandLuck = Luck.REPLACE;
    private boolean protectedItems = true;
    private boolean protectedEntities = false;
    private RewardMode rewardMode = RewardMode.WEIGHT;
    private String playerPlaceHolder = "%player%";

    public ConfigManager(FishingRewards plugin) {
        this.plugin = plugin;
        this.logger = plugin.getFishingLogger();
        if(plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) worldGuardEnabled = true;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        loadConfig();
    }

    public void loadConfig(){
        if(!configFile.exists()) {
            logger.log("Creating default config.yml file...");
            plugin.saveResource("config.yml", false);
            logger.log("config.yml defaults copied.");
        }
        config = YamlConfiguration.loadConfiguration(configFile);


        if(!rewardsFile.exists()) {
            logger.log("Creating default rewards.yml file...");
            plugin.saveResource("rewards.yml", false);
            logger.log("rewards.yml defaults copied.");
        }
        rewardsConfig = new RewardConfiguration(YamlConfiguration.loadConfiguration(rewardsFile));


        String CONFIG_VERSION = "config-version";
        if(config.contains(CONFIG_VERSION)) {
            this.configVersion = config.getInt(CONFIG_VERSION);
            logger.log("Config version " + configVersion + " detected. Checking for updates...");
            if(configVersion != CURRENT_VERSION){
                updateConfig(configVersion);
            }
        }else{
            updateConfig(1);
        }


        String ITEM_CLEAR = "clear-items-after";
        if(config.contains(ITEM_CLEAR)){
            itemClearTime = config.getInt(ITEM_CLEAR);
        }else{
            logger.log(defaultValue(ITEM_CLEAR));
        }

        String LUCK_INCREASE = "luck-increase";
        if(config.contains(LUCK_INCREASE)){
            luckIncrease = config.getDouble(LUCK_INCREASE);
        }else{
            logger.log(defaultValue(LUCK_INCREASE));
        }

        String LUCK_DECREASE = "luck-decrease";
        if(config.contains(LUCK_DECREASE)){
            luckDecrease = config.getDouble(LUCK_DECREASE);
        }else{
            logger.log(defaultValue(LUCK_DECREASE));
        }

        String ALLOW_LUCK_EFFECT = "allow-luck-attribute";
        if(config.contains(ALLOW_LUCK_EFFECT)){
            allowLuckAttribute = config.getBoolean(ALLOW_LUCK_EFFECT);
        }else{
            logger.log(defaultValue(ALLOW_LUCK_EFFECT));
        }

        String ALLOW_OFFHAND_LUCK = "allow-offhand-luck";
        if(config.contains(ALLOW_OFFHAND_LUCK)){
            offhandLuck = Luck.valueOf(config.getString(ALLOW_OFFHAND_LUCK));
        }else{
            logger.log(defaultValue(ALLOW_OFFHAND_LUCK));
        }

        String PLAYER_PROTECTED_ITEMS = "player-protected-items";
        if(config.contains(PLAYER_PROTECTED_ITEMS)){
            protectedItems = config.getBoolean(PLAYER_PROTECTED_ITEMS);
        }else{
            logger.log(defaultValue(PLAYER_PROTECTED_ITEMS));
        }

        String PLAYER_PROTECTED_ENTITIES = "player-protected-entities";
        if(config.contains(PLAYER_PROTECTED_ENTITIES)){
            protectedEntities = config.getBoolean(PLAYER_PROTECTED_ENTITIES);
        }else{
            logger.log(defaultValue(PLAYER_PROTECTED_ENTITIES));
        }

        String REWARD_MODE = "reward-mode";
        if(config.contains(REWARD_MODE)){
            rewardMode = RewardMode.valueOf(config.getString(REWARD_MODE));
        }else{
            logger.log(defaultValue(REWARD_MODE));
        }

        String PLAYER_PLACEHOLDER = "player-placeholder";
        if(config.contains(PLAYER_PLACEHOLDER)){
            playerPlaceHolder = config.getString(PLAYER_PLACEHOLDER);
        }else{
            logger.log(defaultValue(PLAYER_PLACEHOLDER));
        }

    }

    private void updateConfig(int configVersion){
        logger.log("Outdated config version. Updating...");
        switch(configVersion){
            case 1:
                try {
                    if(config.contains("drops")) {
                        int count = 0;

                        for (String line : config.getStringList("drops")) {
                            count++;

                            for(String current : line.split(",")){

                                if(current.startsWith("M:")){
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.ITEM_MATERIAL, current.substring(2).toLowerCase());
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.TYPE, "item");
                                }
                                if(current.startsWith("W:")){
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.CHANCE, Integer.parseInt(current.substring(2)));
                                }
                                if(current.startsWith("N:")){
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.DISPLAY_NAME, current.substring(2));
                                }

                                if(current.startsWith("L:")){
                                    ArrayList<String> lore = new ArrayList<>(Arrays.asList(current.substring(2).split("/n")));
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.ITEM_LORE, lore);
                                }

                                if(current.startsWith("CM:")){
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.CATCH_MESSAGE, current.substring(3));
                                }

                                if(current.startsWith("C:")){
                                    ArrayList<String> commands = new ArrayList<>(Arrays.asList(current.substring(2).split("/n")));
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.COMMANDS, commands);
                                }

                                if(current.startsWith("E:")){
                                    ArrayList<String> enchants = new ArrayList<>();
                                    for(String enchant : Arrays.asList(current.substring(2).split(" "))) {
                                        if (!enchant.equalsIgnoreCase("all")) {
                                            Enchantment mcEnchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchant));
                                            if (mcEnchant != null)
                                                enchants.add(mcEnchant.getKey().getKey() + ":" + mcEnchant.getStartLevel() + "-" + mcEnchant.getMaxLevel());
                                        }else{
                                            for(Enchantment enchantment : Enchantment.values()){
                                                enchants.add(enchantment.getKey().getKey() + ":" + enchantment.getStartLevel() + "-" + enchantment.getMaxLevel() + ":10");
                                            }
                                            break;
                                        }
                                    }
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.ITEM_ENCHANTS, enchants);
                                }

                                if(current.startsWith("EA:")){
                                    String[] ea = current.substring(2).split(" ");
                                    rewardsConfig.set("Reward" + count + "." + RewardConfiguration.ITEM_BONUS_ENCHANTMENT_AMOUNT, Integer.valueOf(ea[0]));
                                }

                                if(current.startsWith("D:")){
                                    if(current.substring(2).startsWith("true")) {
                                        String[] durability = current.substring(7).split(" ");
                                        rewardsConfig.set("Reward" + count + "." + RewardConfiguration.ITEM_DURABILITY, durability.length > 1 ? durability[0] + "-" + durability[1] : durability[0]);
                                    }
                                }

                                if(current.startsWith("A:")){
                                    String[] amount = current.substring(2).split(" ");
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.ITEM_AMOUNT, amount.length > 1 ? amount[0] + "-" + amount[1] : amount[0]);
                                }

                                if(current.startsWith("XP:")){
                                    String[] exp = current.substring(3).split(" ");
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.XP, exp.length > 1 ? exp[0] + "-" + exp[1] : exp[0]);
                                }

                                if(current.startsWith("CN:")){
                                    rewardsConfig.set("Reward"+count+"." + RewardConfiguration.CATCH_MESSAGE, current.substring(3));
                                }
                            }

                        }
                        rewardsConfig.save(rewardsFile);

                    }
                    plugin.saveResource("config.yml", true);
                    logger.log("Successfully updated config.yml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public boolean isWorldGuardEnabled(){
        return worldGuardEnabled;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public int getItemClearTime() {
        return itemClearTime;
    }

    public double getLuckIncrease() {
        return luckIncrease;
    }

    public double getLuckDecrease(){
        return luckDecrease;
    }

    public boolean allowLuckAttribute(){
        return allowLuckAttribute;
    }

    public Luck getOffHandLuck(){
        return offhandLuck;
    }

    public boolean isProtectedItems() {
        return protectedItems;
    }

    public boolean isProtectedEntities() {
        return protectedEntities;
    }

    public RewardMode getRewardMode() {
        return rewardMode;
    }

    //For Debug Purposes
    public void setRewardMode(RewardMode mode) {
        this.rewardMode = mode;
    }

    public File getRewardsFile() {
        return rewardsFile;
    }

    public RewardConfiguration getRewardsConfig() {
        return rewardsConfig;
    }

    public String getPlayerPlaceHolder() {
        return playerPlaceHolder;
    }

    private String defaultValue(String setting){
        return "Review config.yml. Missing'" + setting + "' setting. Using default value.";
    }

    public enum RewardMode {
        WEIGHT,
        ROLL;
    }

    public enum Luck{
        REPLACE,
        ADD,
        DISABLE;
    }

}
