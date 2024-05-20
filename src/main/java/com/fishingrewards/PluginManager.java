package com.fishingrewards;

import com.fishingrewards.rewards.*;
import com.fishingrewards.rewards.RewardAttribute;
import com.fishingrewards.rewards.entity.FishingEntityReward;
import com.fishingrewards.rewards.entity.MobDropContainer;
import com.fishingrewards.rewards.item.EnchantmentContainer;
import com.fishingrewards.rewards.item.FishingItemReward;
import com.fishingrewards.rewards.item.ItemStackBuilder;
import com.fishingrewards.rewards.item.ItemStackContainer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PluginManager implements Listener {

	/**
	 * TODO:
	 * 	Fix/Test the following:
	 * 		- Rework config updater
	 * 		- GUI
	 */


	private final FishingRewards plugin;
	private final PluginLogger logger;
	private final ConfigManager configManager;
	private final Random random = ThreadLocalRandom.current();

	private final ArrayList<FishingReward> rewardsList = new ArrayList<>();

    private final ArrayList<SpawnedReward> spawnedRewards = new ArrayList<>();
	private final ArrayList<UUID> spawnedItems = new ArrayList<>();

	private final String guiName = ChatColor.translateAlternateColorCodes('&', "Rewards: Page ");
	private double totalPages;
	private final int itemsPerPage = 20;
	private final ItemStack BACK = new ItemStackBuilder().setMaterial(Material.ARROW).setName("&8BACK").build();
	private final ItemStack NEXT = new ItemStackBuilder().setMaterial(Material.ARROW).setName("&8NEXT").build();
	private final ItemStack FILLER = new ItemStackBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();

	public PluginManager(FishingRewards plugin){
		this.plugin = plugin;
		this.logger = plugin.getFishingLogger();
		this.configManager = plugin.getConfigManager();
		loadRewards(configManager.getRewardsConfig());
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private void addReward(FishingReward reward){
		rewardsList.add(reward);
	}


	public void reload(){
		rewardsList.clear();
		loadRewards(configManager.getRewardsConfig());
		logger.log("Rewards have been reloaded.");
	}

	private void loadRewards(RewardConfiguration yml){
		Set<String> rewards = yml.getKeys();
		for(String rewardString : rewards){
			FishingReward fishingReward;

			yml.setCurrentReward(rewardString);

			if(!yml.hasRewardType()){
				logger.rewardError(rewardString, RewardConfiguration.TYPE);
				return;
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
						return;
					}

					fishingReward = new FishingEntityReward(yml.getEntityType());

					((FishingEntityReward)fishingReward).setAttributes(yml.getEntityAttributes());
					ArrayList<MobDropContainer> drops = yml.getDrops();
					((FishingEntityReward)fishingReward).setEquipment(yml.getEquipment(drops));
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
			if(yml.hasXP()) fishingReward.setXP(yml.getXP());
			if(yml.hasCatchMessage()) fishingReward.setCatchMessage(yml.getCatchMessage());
			if(yml.hasRegions()) fishingReward.addRegionCondition(yml.getRegions());
			if(yml.hasBiomes()) fishingReward.addBiomeCondition(yml.getBiomes());
			if(yml.hasPermission()) fishingReward.addPermissionCondition(yml.getPermission());
			if(yml.hasTime()) fishingReward.addTimeCondition(yml.getTime());
			if(yml.hasWeather()) fishingReward.setWeatherConditon(yml.getWeather());
			if(yml.isTreasure()) fishingReward.setTreasure();
			if(yml.isJunk()) fishingReward.setJunk();

			addReward(fishingReward);
		}
		totalPages = ((double) rewardsList.size() / itemsPerPage)+1;
		logger.log(rewardsList.size() + " rewards have been loaded.");
	}

	@EventHandler
	public void onFish(PlayerFishEvent e){
		if(e.getState() != PlayerFishEvent.State.CAUGHT_FISH ) return;

		Player player = e.getPlayer();
		ArrayList<FishingReward> currentRewards = new ArrayList<>();
		Location hookLocation = e.getHook().getLocation();
		World world = hookLocation.getWorld();
		if(world == null){
			e.setCancelled(true);
			logger.error("World cannot be null!", false);
			return;
		}

		double totalWeight = 0;

		ItemStack hand = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();

		double luckLevel = 0;
		if(hand.hasItemMeta() && hand.getItemMeta().hasEnchants()){
			if(hand.getItemMeta().hasEnchant(Enchantment.LUCK)) luckLevel = hand.getItemMeta().getEnchantLevel(Enchantment.LUCK);
		}
		if(offHand.hasItemMeta() && offHand.getItemMeta().hasEnchants() && offHand.getItemMeta().hasEnchant(Enchantment.LUCK)) {
			switch (configManager.getOffHandLuck()) {
				case REPLACE:
					luckLevel = offHand.getItemMeta().getEnchantLevel(Enchantment.LUCK);
					break;
				case ADD:
					luckLevel += offHand.getItemMeta().getEnchantLevel(Enchantment.LUCK);
					break;
			}
		}

		if(configManager.allowLuckAttribute()) {
			luckLevel+=player.getAttribute(Attribute.GENERIC_LUCK).getValue();
		}
		double luckMultiplier = ((configManager.getLuckIncrease()/100)*(luckLevel))+1;
		double luckDividier = ((configManager.getLuckDecrease()/100)*(luckLevel))+1;

		for(FishingReward reward : rewardsList){
			boolean add = true;
			if(configManager.isWorldGuardEnabled()) {
				RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
				if (regionContainer != null && reward.hasRegionCondition()) {
					RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
					if(regionManager == null) return;

					for (String region : reward.getRegionCondition()) {
						if (regionManager.hasRegion(region)) {
							if (!regionManager.getRegion(region).contains(hookLocation.getBlockX(), hookLocation.getBlockY(), hookLocation.getBlockZ()))
								add = false;
						}
					}
				}
			}

			if(reward.hasPermissionCondition()) {
				if (!e.getPlayer().hasPermission(reward.getPermissionCondition())) {
					add = false;
				}
			}

			if(reward.hasTimeCondition()) {
				long currentTime = world.getTime();
				if (!(currentTime >= reward.getTimeCondition()[0] && currentTime <= reward.getTimeCondition()[1])) {
					add = false;
				}
			}

			if(reward.hasBiomeConditions()){
				if(!reward.getBiomeCondition().contains(hookLocation.getBlock().getBiome())){
					add = false;
				}
			}
			if(reward.hasThunderCondition()){
				if(!world.isThundering()){
					add = false;
				}
			}
			if(reward.hasRainWeatherCondition()){
				if(!world.hasStorm()){
					add = false;
				}
			}
			if(reward.hasClearWeatherConditon()){
				if(!world.isClearWeather()){
					add = false;
				}
			}

			if(add){
				currentRewards.add(reward);
				double currentWeight = reward.getChance();
				if(luckLevel > 0) {
					if (reward.isTreasure()) currentWeight *= luckMultiplier;
					if (reward.isJunk()) currentWeight /= luckDividier;
				}
				totalWeight+=currentWeight;
			}
		}

		switch(configManager.getRewardMode()){
			case WEIGHT:

				double rollWeight = random.nextDouble(totalWeight);
				double cumulativeWeight = 0;
				for(FishingReward reward : currentRewards){
					double weight = reward.getChance();
					if(luckMultiplier > 0) {
						if(reward.isTreasure()) weight *= luckMultiplier;
						if(reward.isJunk()) weight /= luckDividier;
					}
					cumulativeWeight+=weight;
					if(rollWeight < cumulativeWeight){
						logger.log("Rolled: " + rollWeight + "Out of " + totalWeight + "/Weight: " + weight + "/Cumulative: " + cumulativeWeight + "/Luck Multiplier: " + luckMultiplier + "/Luck Divider: " + luckDividier);
						attachToRod(e, reward);
						break;
					}
				}

				break;
			case ROLL:
				if(!currentRewards.isEmpty()){
					Collections.sort(currentRewards);
				}
				double highestChance = currentRewards.get(currentRewards.size()-1).getChance();
				double roll = random.nextDouble(0, highestChance);
				for(FishingReward reward : currentRewards){
					if(roll <= reward.getChance()){
						attachToRod(e, reward);
						break;
					}
				}
				break;
		}
	}

	private void attachToRod(PlayerFishEvent e, FishingReward reward){
		e.setExpToDrop(random.nextInt(reward.getMinXP(), reward.getMaxXP()+1));
		Entity entity = null;
		if(reward instanceof FishingItemReward itemReward){

			if (e.getCaught() instanceof Item) ((Item) e.getCaught()).setItemStack(new ItemStackBuilder().buildFromContainer(logger, itemReward.getItemStackContainer(), random).build());
			entity = e.getCaught();

		}else if(reward instanceof FishingEntityReward entityReward){
			entity = e.getPlayer().getWorld().spawnEntity(e.getHook().getLocation(), entityReward.getEntityType());
			entity.setCustomName(ChatColor.translateAlternateColorCodes('&', entityReward.getName()));
			entity.setCustomNameVisible(true);
			if(entity instanceof LivingEntity livingEntity){
				for(RewardAttribute entityAttribute : RewardAttribute.values()) {
					if(entityReward.hasAttribute(entityAttribute.getAttribute())){
						livingEntity.getAttribute(entityAttribute.getAttribute()).setBaseValue(entityReward.getAttribute(entityAttribute.getAttribute()));
					}
				}
				for(EquipmentSlot equipmentSlot : EquipmentSlot.values()){
					if(entityReward.getEquipment(equipmentSlot) != null) livingEntity.getEquipment().setItem(equipmentSlot, new ItemStackBuilder().buildFromContainer(logger, entityReward.getEquipment(equipmentSlot), random).build());
				}
				livingEntity.setHealth(entityReward.getAttribute(Attribute.GENERIC_MAX_HEALTH));
			}


			if (e.getCaught() instanceof Item) e.getCaught().remove();
			e.getHook().setHookedEntity(entity);
		}

		if(entity == null){
			logger.error("Entity failed to spawn, entity cannot be null.", false);
			e.setCancelled(true);
			return;
		}


		for(String command : reward.getCommandsRan()){
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace(configManager.getPlayerPlaceHolder(), e.getPlayer().getName()));
		}

		SpawnedReward spawnedReward = new SpawnedReward(reward, e.getPlayer().getUniqueId(), entity.getUniqueId());
		spawnedRewards.add(spawnedReward);


		if(reward.getCatchMessage() != null && !reward.getCatchMessage().isEmpty()) e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getCatchMessage()));
		if(configManager.getItemClearTime() > 0) spawnedReward.scheduleDespawn(plugin, plugin.getConfigManager(), entity);
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent e){
		if(e.getEntity() instanceof Player player){

			if(spawnedRewards.isEmpty()) return;

			SpawnedReward spawnedReward = getSpawnedReward(e.getItem().getUniqueId());
			if(spawnedReward == null) return;
			if(spawnedReward.getPlayerUUID() != player.getUniqueId()){
				e.setCancelled(true);
				return;
			}

			if(spawnedReward.getReward() instanceof FishingItemReward itemReward){
				if(itemReward.isConsumed()){
					e.setCancelled(true);
					e.getItem().remove();
				}
			}
			spawnedRewards.remove(spawnedReward);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player player){

			if(spawnedRewards.isEmpty()) return;

			SpawnedReward spawnedReward = getSpawnedReward(e.getEntity().getUniqueId());
			if(spawnedReward == null) return;
			if(!configManager.isProtectedEntities()) return;
			if(spawnedReward.getPlayerUUID() != player.getUniqueId()) e.setCancelled(true);

		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if(spawnedRewards.isEmpty()) return;

		SpawnedReward spawnedReward = getSpawnedReward(e.getEntity().getUniqueId());
		if(spawnedReward == null) return;
		if(spawnedReward.getRewardUUID() != e.getEntity().getUniqueId()) return;

		if(spawnedReward.getReward() instanceof FishingEntityReward entityReward){

			spawnedRewards.remove(spawnedReward);

			if(!entityReward.getDrops().isEmpty()) {
				e.getDrops().clear();
				ArrayList<ItemStack> drops = new ArrayList<>();

				for (MobDropContainer mobDrop : entityReward.getDrops()) {
					double chance = random.nextDouble(0, 100);
					if (chance <= mobDrop.getChance()) {
						drops.add(new ItemStackBuilder().buildFromContainer(logger, mobDrop.getItemStackContainer(), random).build());
					}

				}

				for(ItemStack stack : drops){
					Entity entity = e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), stack);
					spawnedRewards.add(new SpawnedReward(null, spawnedReward.getPlayerUUID(), entity.getUniqueId()));
				}
			}
		}
	}


	public SpawnedReward getSpawnedReward(UUID entityUUID){
		for (SpawnedReward spawnedReward : spawnedRewards) {
			if (spawnedReward.getRewardUUID().equals(entityUUID)){
				return spawnedReward;
			}
		}
		return null;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if(e.getWhoClicked() instanceof Player player){
			if(e.getView().getTitle().startsWith(guiName)){
				e.setCancelled(true);
				if(e.getCurrentItem() == null) return;
				int currentPage = Integer.parseInt(e.getView().getTitle().replace(guiName, ""))-1;
				if(e.getCurrentItem().isSimilar(NEXT)){
					if(currentPage+1 <= totalPages) {
						openRewardGUI(player, currentPage+1);
						return;
					}
				}
				if(e.getCurrentItem().isSimilar(BACK)){
					if(currentPage-1 > 0){
						openRewardGUI(player, currentPage-1);
					}
				}
			}
		}

	}


	public void openRewardGUI(Player player, int page){
		if(page > totalPages) return;

		Inventory inventory = Bukkit.createInventory(null, 54, guiName + (page+1));

		int slot = 0;
		for(int i = itemsPerPage*page; i < itemsPerPage*(page+1); i++){
			if(i == rewardsList.size()) break;
			FishingReward reward = rewardsList.get(i);
			ItemStackBuilder builder = new ItemStackBuilder();
			if(reward instanceof FishingItemReward itemReward){
				ItemStackContainer container = itemReward.getItemStackContainer();
				builder.setMaterial(container.getMaterial());
				if(itemReward.getName() != null && !itemReward.getName().isEmpty()) builder.setName(itemReward.getName());
				if(!container.getLore().isEmpty()) builder.addLore(container.getLore());
			}else if(reward instanceof FishingEntityReward entityReward){
				builder.setMaterial(Material.getMaterial(entityReward.getEntityType().toString()+"_SPAWN_EGG"));
			}

			builder.spaceLore();

			String chance = "&8";
			switch(configManager.getRewardMode()){
				default:
				case WEIGHT:
					chance += "Weight: &f" + reward.getChance();
					break;
				case ROLL:
					chance += "Chance: &f" + reward.getChance() + "%";
					break;
			}
			builder.addLore(chance);

			if(reward instanceof FishingItemReward itemReward){
				ItemStackContainer container = itemReward.getItemStackContainer();
				builder.addLore("&8Type: &fItem");
				builder.addLore("&8Material: &f" + container.getMaterial());
				builder.addLore("&8Amount: &f" + (container.getMinAmount() != container.getMaxAmount() ? container.getMinAmount() + "-" + container.getMaxAmount() : container.getMinAmount()));
				if(container.hasDurability()) builder.addLore("&8Durability: &f" + container.getMinDurability());
				builder.addLore("&8Consumed: &f" + itemReward.isConsumed());
				if(!container.getGuaranteedEnchantments().isEmpty()) builder.addLore("&8Enchantments:");
				for(EnchantmentContainer enchantment : container.getGuaranteedEnchantments()){
					builder.addLore(" &8- &f" + enchantment.getEnchantment().getKey() + " " + enchantment.getMinLevel() + "-" + enchantment.getMaxLevel() + ". " + enchantment.getChance() + "%");
				}
				if(container.hasGuaranteedEnchantmentsAmount()) builder.addLore("&8Guaranteed Enchantments Amount: &f" + container.getGuaranteedEnchantmentsAmount());
				if(container.hasBonusEnchantmentsAmount()) builder.addLore("&8Bonus Enchantments Amount: &f" + container.getBonusEnchantmentsAmount());
				if(container.overrideBonusEnchantments()) builder.addLore("&8Override Bonus Enchantments: &f" + container.overrideBonusEnchantments());
			}
			if(reward instanceof FishingEntityReward entityReward){
				builder.addLore("&8Type: &fEntity");
				builder.addLore("&8Entity Type: &f" + entityReward.getEntityType().toString());
				if(entityReward.hasAttributes()) {
					builder.addLore("&8Attributes:");
					for (Attribute attribute : Attribute.values()) {
						if (entityReward.hasAttribute(attribute)) builder.addLore(" &8- &f" + attribute.toString() + " " + entityReward.getAttribute(attribute));
					}
				}
				if(entityReward.hasDrops()){
					builder.addLore("&8Drops:");
					for(MobDropContainer drop : entityReward.getDrops()){
						ItemStackContainer container = drop.getItemStackContainer();
						builder.addLore(" &8- &f" + container.getMaterial() + " " + container.getMinAmount() + "-" + container.getMaxAmount() + " " + drop.getChance() + "%");
					}
				}
				if(entityReward.hasHelmet()) builder.addLore("&8Helmet: &f" + entityReward.getHelmet().getMaterial());
				if(entityReward.hasChestplate()) builder.addLore("&8Chestplate: &f" + entityReward.getChestplate().getMaterial());
				if(entityReward.hasLeggings()) builder.addLore("&8Leggings: &f" + entityReward.getLeggings().getMaterial());
				if(entityReward.hasBoots()) builder.addLore("&8Boots: &f" + entityReward.getBoots().getMaterial());
				if(entityReward.hasHand()) builder.addLore("&8Hand: &f" + entityReward.getHand().getMaterial());
				if(entityReward.hasOffhand()) builder.addLore("&8Off Hand: &f" + entityReward.getOffhand().getMaterial());
			}
			builder.addLore("&8XP: &f" + (reward.getMinXP() != reward.getMaxXP() ? reward.getMinXP() + "-" + reward.getMaxXP() : reward.getMinXP()));
			if(reward.isTreasure()) builder.addLore("&8Treasure: &ftrue");
			if(reward.isJunk()) builder.addLore("&8Junk: &ftrue");


			if(reward.getName() != null && !reward.getName().isEmpty() )builder.addLore("&8Name: &f" + reward.getName());
			if(reward.getCatchMessage() != null && !reward.getCatchMessage().isEmpty()) builder.addLore("&8Catch Message: &f" + reward.getCatchMessage());
			if(!reward.getCommandsRan().isEmpty()){
				builder.addLore("&8Commands:");
				for(String command : reward.getCommandsRan()){
					builder.addLore(" &8- &f" + command);
				}
			}

			if(reward.hasRegionCondition()){
				builder.addLore("&8Regions:");
				for(String region : reward.getRegionCondition()){
					builder.addLore(" &8- &f" + region);
				}
			}

			if(reward.hasBiomeConditions()){
				builder.addLore("&8Biomes:");
				for(Biome biome : reward.getBiomeCondition()){
					builder.addLore(" &8- &f" + biome);
				}
			}

			if(reward.hasPermissionCondition()){
				builder.addLore("&8Permission: &f" + reward.getPermissionCondition());
			}

			if(reward.hasTimeCondition()){
				builder.addLore("&8Time: &f" + reward.getTimeCondition()[0] + "-" + reward.getTimeCondition()[1]);
			}

			inventory.setItem(slot, builder.build());
			slot++;
		}

		if(page > 0) inventory.setItem(45, BACK);
		if(page < totalPages-1) inventory.setItem(53, NEXT);

		for(int i = 0; i < inventory.getSize(); i++){
			if(inventory.getItem(i) == null) inventory.setItem(i, FILLER);
		}

		player.openInventory(inventory);

	}

	public ArrayList<SpawnedReward> getSpawnedRewards() {
		return spawnedRewards;
	}

}
