package com.fishingrewards.listeners;

import com.fishingrewards.ConfigManager;
import com.fishingrewards.FishingRewards;
import com.fishingrewards.rewards.FishingReward;
import com.fishingrewards.rewards.RewardManager;
import com.fishingrewards.rewards.entity.FishingEntityReward;
import com.fishingrewards.rewards.entity.MobDropContainer;
import com.fishingrewards.rewards.item.EnchantmentContainer;
import com.fishingrewards.rewards.item.FishingItemReward;
import com.fishingrewards.rewards.item.ItemStackBuilder;
import com.fishingrewards.rewards.item.ItemStackContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIHandler implements Listener {

    private FishingRewards plugin;
    private ConfigManager configManager;
    private RewardManager rewardManager;

    private final String guiName = ChatColor.translateAlternateColorCodes('&', "Rewards: Page ");
    private final ItemStack BACK = new ItemStackBuilder().setMaterial(Material.ARROW).setName("&8BACK").build();
    private final ItemStack NEXT = new ItemStackBuilder().setMaterial(Material.ARROW).setName("&8NEXT").build();
    private final ItemStack FILLER = new ItemStackBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
    private double totalPages;
    private final int itemsPerPage = 45;

    public GUIHandler(FishingRewards plugin){
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.rewardManager = plugin.getRewardManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        load();
    }

    public void load(){
        totalPages = ((double) rewardManager.getRewardsList().size() / itemsPerPage)+1;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player player){
            if(e.getView().getTitle().startsWith(guiName)){
                e.setCancelled(true);
                if(e.getCurrentItem() == null) return;
                int currentPage = Integer.parseInt(e.getView().getTitle().replace(guiName, ""))-1;
                if(e.getCurrentItem().isSimilar(NEXT)){
                    if(currentPage+1 < (int)totalPages) {
                        openRewardGUI(player, currentPage+1);
                        return;
                    }
                }
                if(e.getCurrentItem().isSimilar(BACK)){
                    if(currentPage > 0){
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
            if(i == rewardManager.getRewardsList().size()) break;
            FishingReward reward = rewardManager.getRewardsList().get(i);
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
        if(page < (int)totalPages-1) inventory.setItem(53, NEXT);

        for(int i = 0; i < inventory.getSize(); i++){
            if(inventory.getItem(i) == null) inventory.setItem(i, FILLER);
        }

        player.openInventory(inventory);

    }

}
