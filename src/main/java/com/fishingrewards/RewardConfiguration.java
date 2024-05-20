package com.fishingrewards;

import com.fishingrewards.rewards.*;
import com.fishingrewards.rewards.entity.MobDropContainer;
import com.fishingrewards.rewards.item.EnchantmentContainer;
import com.fishingrewards.rewards.item.ItemStackContainer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RewardConfiguration {

    public static final String CHANCE = "chance";
    public static final String TYPE = "type";
    public static final String XP = "xp";
    public static final String COMMANDS = "commands";
    public static final String CATCH_MESSAGE = "message";
    public static final String CONSUMED = "consumed";
    public static final String TREASURE = "treasure";
    public static final String JUNK = "junk";
    public static final String DISPLAY_NAME = "name";

    public static final String ITEM_MATERIAL = "material";
    public static final String ITEM_AMOUNT = "amount";
    public static final String ITEM_MODEL_DATA = "model-data";
    public static final String ITEM_LORE = "lore";
    public static final String ITEM_ENCHANTS = "enchantments";
    public static final String ITEM_GUARANTEED_ENCHANTMENT_AMOUNT = "guaranteed-enchantment-amount";
    public static final String ITEM_BONUS_ENCHANTMENT_AMOUNT = "bonus-enchantment-amount";
    public static final String ITEM_BONUS_ENCHNTMENT_OVERRIDE = "override-bonus-enchantments";
    public static final String ITEM_FLAGS = "item-flags";
    public static final String ITEM_DURABILITY = "durability";
    public static final String ITEM_UNBREAKABLE = "unbreakable";
    public static final String ITEM_ATTRIBUTES = "attributes";
    public static final String ITEM_ATTRIBUTE_VALUE = "value";
    public static final String ITEM_ATTRIBUTE_OPERATION = "operation";
    public static final String ITEM_ATTRIBUTE_EQUIPMENT_SLOT = "equipment-slot";
    public static final String ITEM_DAMAGED = "damaged";

    public static final String ENTITY_TYPE = "entity-type";
    public static final String ENTITY_ATTRIBUTES = "attributes";
    public static final String DROPS = "drops";
    public static final String EQUIPMENT = "equipment";

    public static final String REGIONS = "regions";
    public static final String BIOMES = "biomes";
    public static final String PERMISSION = "permission";
    public static final String TIME = "time";
    public static final String WEATHER = "weather";

    private YamlConfiguration yml;
    private final String DOT = ".";
    private String currentReward;

    public RewardConfiguration(YamlConfiguration ymlConfiguration){
        this.yml = ymlConfiguration;
    }

    public void setCurrentReward(String reward){
        this.currentReward = reward + DOT;
    }

    public boolean hasRewardType(){
        return yml.contains(currentReward + TYPE);
    }

    public RewardType getRewardType(){
        return RewardType.valueOf(yml.getString(currentReward + TYPE).toUpperCase());
    }

    public boolean hasChance(){
        return yml.contains(currentReward + CHANCE);
    }

    public double getChance(){
        return yml.getDouble(currentReward + CHANCE);
    }

    public boolean hasXP(){
        return yml.contains(currentReward + XP);
    }

    public int[] getXP(){
        int[] xp = new int[2];
        String[] tokens = yml.getString(currentReward + XP).split("-");
        xp[0] = Integer.parseInt(tokens[0]);
        xp[1] = xp[0];
        if(tokens.length > 1) xp[1] = Integer.parseInt(tokens[1]);
        return xp;
    }

    public boolean hasDisplayName(){
        return yml.contains(currentReward + DISPLAY_NAME);
    }

    public String getDisplayName(){
        return yml.getString(currentReward + DISPLAY_NAME);
    }

    public boolean hasCommands(){
        return yml.contains(currentReward + COMMANDS);
    }

    public ArrayList<String> getComamnds(){
        return new ArrayList<>(yml.getStringList(currentReward + COMMANDS));
    }

    public boolean hasCatchMessage(){
        return yml.contains(currentReward + CATCH_MESSAGE);
    }

    public String getCatchMessage(){
        return yml.getString(currentReward + CATCH_MESSAGE);
    }

    public boolean isTreasure(){
        return yml.contains(currentReward + TREASURE) && yml.getBoolean(currentReward + TREASURE);
    }

    public boolean isJunk(){
        return yml.contains(currentReward + JUNK) && yml.getBoolean(currentReward + JUNK);
    }

    public boolean isConsumed(){
        return yml.contains(currentReward + CONSUMED) && yml.getBoolean(currentReward + CONSUMED);
    }

    public boolean hasRegions(){
        return yml.contains(currentReward + REGIONS);
    }

    public List<String> getRegions(){
        return yml.getStringList(currentReward + REGIONS);
    }

    public boolean hasBiomes(){
        return yml.contains(currentReward + BIOMES);
    }

    public List<Biome> getBiomes(){
        List<Biome> biomes = new ArrayList<>();
        for(String b : yml.getStringList(currentReward + BIOMES)){
            biomes.add(Biome.valueOf(b));
        }
        return biomes;
    }

    public boolean hasPermission(){
        return yml.contains(currentReward + PERMISSION);
    }

    public String getPermission(){
        return yml.getString(currentReward + PERMISSION);
    }

    public boolean hasTime(){
        return yml.contains(currentReward + TIME);
    }

    public int[] getTime(){
        int[] time = new int[2];
        String[] t = yml.getString(currentReward + TIME).split("-");
        time[0] = Integer.parseInt(t[0]);
        time[1] = Integer.parseInt(t[1]);
        return time;
    }

    public boolean hasWeather(){
        return yml.contains(currentReward + WEATHER);
    }

    public WeatherCondition getWeather(){
        return WeatherCondition.valueOf(yml.getString(currentReward + WEATHER));
    }

    public boolean hasEntityType(){
        return yml.contains(currentReward + ENTITY_TYPE);
    }

    public EntityType getEntityType(){
        return EntityType.valueOf(yml.getString(currentReward + ENTITY_TYPE));
    }

    public boolean hasEntityAttributes(){
        return yml.contains(currentReward + ENTITY_ATTRIBUTES);
    }

    public HashMap<Attribute, Double> getEntityAttributes(){
        HashMap<Attribute, Double> map = new HashMap<>();
        ConfigurationSection section = yml.getConfigurationSection(currentReward + ENTITY_ATTRIBUTES);
        for(RewardAttribute rewardAttribute : RewardAttribute.values()){
            if(section.contains(rewardAttribute.getConfigID())){
                map.put(rewardAttribute.getAttribute(), section.getDouble(rewardAttribute.getConfigID()));
            }
        }
        return map;
    }

    public boolean hasDrops(){
        return yml.contains(currentReward + DROPS);
    }

    public ArrayList<MobDropContainer> getDrops(){
        ArrayList<MobDropContainer> drops = new ArrayList<>();
        ConfigurationSection section = yml.getConfigurationSection(currentReward + DROPS);

        for(String current : section.getKeys(false)){
            boolean damaged = false;
            if(section.contains(current + ITEM_DAMAGED)) damaged = section.getBoolean(current + ITEM_DAMAGED);
            MobDropContainer mobDropContainer = new MobDropContainer(buildItemStackContainer(section.getCurrentPath() + DOT + current), section.getDouble(current + DOT + CHANCE), damaged);
            drops.add(mobDropContainer);
        }
        return drops;
    }

    public boolean hasEquipment(){
        return yml.contains(currentReward + EQUIPMENT);
    }

    public HashMap<EquipmentSlot, ItemStackContainer> getEquipment(ArrayList<MobDropContainer> drops){
        HashMap<EquipmentSlot, ItemStackContainer> map = new HashMap<>();

        for(EquipmentSlot slot : EquipmentSlot.values()){
            ConfigurationSection section = yml.getConfigurationSection(currentReward + EQUIPMENT);

            if(section.contains(slot.toString())){
                ItemStackContainer itemStackContainer = buildItemStackContainer(section.getCurrentPath() + DOT + slot);
                map.put(slot, itemStackContainer);
                if(section.contains(slot + DOT + DROPS)){
                    double chance = section.getDouble(slot + DOT + DROPS + DOT + CHANCE);
                    boolean damaged = section.getBoolean(slot + DOT + DROPS + DOT + ITEM_DAMAGED);
                    drops.add(new MobDropContainer(itemStackContainer, chance, damaged));
                }
            }
        }

        return map;
    }


    public ItemStackContainer buildItemStackContainer(String path){
        ItemStackContainer container = new ItemStackContainer(Material.getMaterial(yml.getString(path + DOT + ITEM_MATERIAL).toUpperCase()));
        if(yml.contains(path + DOT + ITEM_AMOUNT)){
            String[] amount = yml.getString(path + DOT + ITEM_AMOUNT).split("-");
            int minAmount = Integer.parseInt(amount[0]);
            int maxAmount = minAmount;
            if(amount.length > 1){
                maxAmount = Integer.parseInt(amount[1]);
            }
            container.setAmount(minAmount, maxAmount);
        }

        if(yml.contains(path + DOT + DISPLAY_NAME)) container.setDisplayName(yml.getString(path + DOT + DISPLAY_NAME));
        if(yml.contains(path + DOT + ITEM_LORE)) container.addLore(yml.getStringList(path + DOT + ITEM_LORE));
        if(yml.contains(path + DOT + ITEM_MODEL_DATA)) container.setModelData(yml.getInt(path + DOT + ITEM_MODEL_DATA));

        if(yml.contains(path + DOT + ITEM_ENCHANTS)){
            for(String s : yml.getStringList(path + DOT + ITEM_ENCHANTS)){
                String[] split = s.split(":");
                String[] level = split[1].split("-");
                int minLevel = Integer.parseInt(level[0]);
                int maxLevel = minLevel;
                if(level.length > 1) maxLevel = Integer.parseInt(level[1]);
                double chance = 100;
                if(split.length > 2) chance = Double.parseDouble(split[2]);
                EnchantmentContainer enchantmentContainer = new EnchantmentContainer(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(split[0].toLowerCase())), minLevel, maxLevel, chance);
                if(enchantmentContainer.isGuaranteed()){
                    container.addGuaranteedEnchantment(enchantmentContainer);
                }else{
                    container.addBonusEnchantment(enchantmentContainer);
                }
            }
        }

        if(yml.contains(path + DOT + ITEM_GUARANTEED_ENCHANTMENT_AMOUNT)) container.setGuaranteedEnchantmentsAmount(yml.getInt(path + DOT + ITEM_GUARANTEED_ENCHANTMENT_AMOUNT));
        if(yml.contains(path + DOT + ITEM_BONUS_ENCHANTMENT_AMOUNT)) container.setBonusEnchantmentsAmount(yml.getInt(path + DOT + ITEM_BONUS_ENCHANTMENT_AMOUNT));
        if(yml.contains(path + DOT + ITEM_BONUS_ENCHNTMENT_OVERRIDE)) container.setOverrideBonusEnchantments(yml.getBoolean(path + DOT + ITEM_BONUS_ENCHNTMENT_OVERRIDE));
        if(yml.contains(path + DOT + ITEM_FLAGS)) container.addFlags(yml.getStringList(path + DOT + ITEM_FLAGS));

        if(yml.contains(path + DOT + ITEM_DURABILITY)){
           String[] durability = yml.getString(path + DOT + ITEM_DURABILITY).split("-");
            int minDurability = Integer.parseInt(durability[0]);
            int maxDurability = minDurability;
            if(durability.length > 1) maxDurability = Integer.parseInt(durability[1]);
            container.setDurability(minDurability, maxDurability);
        }

        if(yml.contains(path + DOT + ITEM_UNBREAKABLE)){
            if(yml.getBoolean(path + DOT + ITEM_UNBREAKABLE)) container.setUnbreakable();
        }

        if(yml.contains(path + DOT + ITEM_ATTRIBUTES)){
            Set<String> attributes = yml.getConfigurationSection(path + DOT + ITEM_ATTRIBUTES).getKeys(false);
            for(RewardAttribute attribute : RewardAttribute.values()){
                if(attributes.contains(attribute.getConfigID())) container.addAttribute(attribute.getAttribute(), "reward", yml.getDouble(path + DOT + ITEM_ATTRIBUTES + DOT + attribute.getConfigID() + DOT + ITEM_ATTRIBUTE_VALUE), AttributeModifier.Operation.valueOf(yml.getString(path + DOT + ITEM_ATTRIBUTES + DOT + attribute.getConfigID() + DOT + ITEM_ATTRIBUTE_OPERATION)), EquipmentSlot.valueOf(yml.getString(path + DOT + ITEM_ATTRIBUTES + DOT + attribute.getConfigID() + DOT + ITEM_ATTRIBUTE_EQUIPMENT_SLOT)));
            }
        }

        return container;
    }

    public Set<String> getKeys(){
        return yml.getKeys(false);
    }

    public void set(String string, Object object){
        yml.set(string, object);
    }

    public void save(File file) throws IOException {
        yml.save(file);
    }

    public YamlConfiguration getConfiguration() {
        return yml;
    }
}
