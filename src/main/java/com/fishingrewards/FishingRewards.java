package com.fishingrewards;

import com.fishingrewards.commands.FishingRewardsCommand;
import com.fishingrewards.managers.ConfigManager;
import com.fishingrewards.managers.FishingManager;
import com.fishingrewards.managers.GUIHandler;
import com.fishingrewards.managers.RewardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingRewards extends JavaPlugin {

    private PluginLogger logger;
    private ConfigManager configManager;
    private RewardManager rewardManager;
    private FishingManager fishingManager;
    private GUIHandler guiHandler;
    private FishingRewardsCommand fishingRewardsCommand;


    @Override
    public void onEnable() {
        logger = new PluginLogger(this);
        configManager = new ConfigManager(this);
        rewardManager = new RewardManager(this);
        fishingManager = new FishingManager(this);
        guiHandler = new GUIHandler(this);
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

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public FishingManager getFishingManager() {
        return fishingManager;
    }

    public GUIHandler getGuiHandler() {
        return guiHandler;
    }
}
