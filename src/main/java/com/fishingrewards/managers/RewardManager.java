package com.fishingrewards.managers;

import com.fishingrewards.FishingRewards;
import com.fishingrewards.PluginLogger;
import com.fishingrewards.rewards.FishingReward;
import com.fishingrewards.rewards.RewardConfiguration;
import com.fishingrewards.rewards.entity.FishingEntityReward;
import com.fishingrewards.rewards.entity.MobDropContainer;
import com.fishingrewards.rewards.item.FishingItemReward;
import com.fishingrewards.rewards.item.ItemStackContainer;

import java.util.*;

public class RewardManager {

	private final FishingRewards plugin;
	private final PluginLogger logger;
	private final ConfigManager configManager;

	private final ArrayList<FishingReward> rewardsList = new ArrayList<>();

	public RewardManager(FishingRewards plugin){
		this.plugin = plugin;
		this.logger = plugin.getFishingLogger();
		this.configManager = plugin.getConfigManager();
		loadRewards();
	}

	private void loadRewards(){
		RewardConfiguration yml = configManager.getRewardsConfig();
		Set<String> rewards = yml.getKeys();
		for(String rewardString : rewards){
			FishingReward fishingReward;

			yml.setCurrentReward(rewardString);

			if(!yml.hasRewardType()){
				logger.rewardError(rewardString, RewardConfiguration.TYPE);
				continue;
			}

			switch(yml.getRewardType()){
				case ITEM:
				default:

					ItemStackContainer itemStackContainer = yml.buildItemStackContainer(rewardString);

					fishingReward = new FishingItemReward(itemStackContainer);
					if(yml.isConsumed()) ((FishingItemReward) fishingReward).setConsumed();

					break;
				case ENTITY:
					if(!yml.hasEntityType()){
						logger.rewardError(rewardString, RewardConfiguration.ENTITY_TYPE);
						continue;
					}

					fishingReward = new FishingEntityReward(yml.getEntityType());

					if(yml.hasEntityAttributes()) ((FishingEntityReward)fishingReward).setAttributes(yml.getEntityAttributes());
					ArrayList<MobDropContainer> drops = new ArrayList<>();
					if(yml.hasDrops()) drops.addAll(yml.getDrops());
					if(yml.hasEquipment()) ((FishingEntityReward)fishingReward).setEquipment(yml.getEquipment(drops));
					((FishingEntityReward)fishingReward).setDrops(drops);
					break;

			}

			if(!yml.hasChance()) {
				logger.rewardError(rewardString, RewardConfiguration.CHANCE);
				return;
			}
			double chance = yml.getChance();
			if(configManager.getRewardMode() == ConfigManager.RewardMode.ROLL && yml.getChance() > 100) chance = 100;
			fishingReward.setChance(chance);

			if(yml.hasCommands()) fishingReward.setCommandsRan(yml.getComamnds());
			if(yml.hasDisplayName()) fishingReward.setName(yml.getDisplayName());
			if(yml.hasRewardSound()) fishingReward.setRewardSound(yml.getRewardSound());
			if(yml.hasXP()) fishingReward.setXP(yml.getXP());
			if(yml.hasCatchMessage()) fishingReward.setCatchMessage(yml.getCatchMessage());
			if(yml.hasRegions()) fishingReward.addRegionCondition(yml.getRegions());
			if(yml.hasBiomes()) fishingReward.addBiomeCondition(yml.getBiomes());
			if(yml.hasPermission()) fishingReward.addPermissionCondition(yml.getPermission());
			if(yml.hasTime()) fishingReward.addTimeCondition(yml.getTime());
			if(yml.hasWeather()) fishingReward.setWeatherConditon(yml.getWeather());
			if(yml.isTreasure()) fishingReward.setTreasure();
			if(yml.isJunk()) fishingReward.setJunk();

			rewardsList.add(fishingReward);
		}
		logger.log(rewardsList.size() + " rewards have been loaded.");
	}

	public void reload(){
		rewardsList.clear();
		loadRewards();
	}

	public ArrayList<FishingReward> getRewardsList() {
		return rewardsList;
	}



}
