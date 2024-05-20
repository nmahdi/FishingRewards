package com.fishingrewards;

import com.fishingrewards.commands.FishingRewardsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingRewards extends JavaPlugin {

    private PluginLogger logger;
    private ConfigManager configManager;
    private PluginManager pluginManager;
    private FishingRewardsCommand fishingRewardsCommand;


    @Override
    public void onEnable() {
        logger = new PluginLogger(this);
        configManager = new ConfigManager(this);
        pluginManager = new PluginManager(this);
        fishingRewardsCommand = new FishingRewardsCommand(this);
    }

    @Override
    public void onDisable() {

    }

    public PluginLogger getFishingLogger(){
        return logger;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}
