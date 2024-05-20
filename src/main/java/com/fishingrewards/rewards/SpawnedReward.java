package com.fishingrewards.rewards;

import com.fishingrewards.FishingRewards;
import com.fishingrewards.ConfigManager;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SpawnedReward {

    private FishingReward reward;
    private UUID playerUUID;
    private UUID dropUUID;

    public SpawnedReward(FishingReward reward, UUID playerUUID, UUID dropUUID) {
        this.reward = reward;
        this.playerUUID = playerUUID;
        this.dropUUID = dropUUID;
    }

    public FishingReward getReward() {
        return reward;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getRewardUUID() {
        return dropUUID;
    }

    public void scheduleDespawn(FishingRewards plugin, ConfigManager settings, Entity entity){
        new BukkitRunnable(){
            public void run(){
                if(entity != null) entity.remove();
            }
        }.runTaskLater(plugin, settings.getItemClearTime()* 20L);
    }

}
