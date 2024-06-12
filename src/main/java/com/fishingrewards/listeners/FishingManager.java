package com.fishingrewards.listeners;

import com.fishingrewards.ConfigManager;
import com.fishingrewards.FishingRewards;
import com.fishingrewards.PluginLogger;
import com.fishingrewards.rewards.FishingReward;
import com.fishingrewards.rewards.RewardAttribute;
import com.fishingrewards.rewards.RewardManager;
import com.fishingrewards.rewards.SpawnedReward;
import com.fishingrewards.rewards.entity.FishingEntityReward;
import com.fishingrewards.rewards.entity.MobDropContainer;
import com.fishingrewards.rewards.item.FishingItemReward;
import com.fishingrewards.rewards.item.ItemStackBuilder;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FishingManager implements Listener {

    private Random random = ThreadLocalRandom.current();
    private FishingRewards plugin;
    private PluginLogger logger;
    private ConfigManager configManager;
    private RewardManager rewardManager;

    private final ArrayList<SpawnedReward> spawnedRewards = new ArrayList<>();

    public FishingManager(FishingRewards plugin){
        this.plugin = plugin;
        this.logger = plugin.getFishingLogger();
        this.configManager = plugin.getConfigManager();
        this.rewardManager = plugin.getRewardManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

        for(FishingReward reward : rewardManager.getRewardsList()){
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

            if (e.getCaught() instanceof Item) ((Item) e.getCaught()).setItemStack(new ItemStackBuilder().buildFromContainer(itemReward.getItemStackContainer(), random).build());
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
                    if(entityReward.getEquipment(equipmentSlot) != null) livingEntity.getEquipment().setItem(equipmentSlot, new ItemStackBuilder().buildFromContainer(entityReward.getEquipment(equipmentSlot), random).build());
                }
                if(entityReward.hasAttribute(Attribute.GENERIC_MAX_HEALTH)) livingEntity.setHealth(entityReward.getAttribute(Attribute.GENERIC_MAX_HEALTH));
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
        getSpawnedRewards().add(spawnedReward);


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
                        drops.add(new ItemStackBuilder().buildFromContainer(mobDrop.getItemStackContainer(), random).build());
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

    public ArrayList<SpawnedReward> getSpawnedRewards() {
        return spawnedRewards;
    }


}
