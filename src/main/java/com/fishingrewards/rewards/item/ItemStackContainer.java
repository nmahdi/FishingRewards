package com.fishingrewards.rewards.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemStackContainer {

    private final Material material;
    private int minAmount = 1;
    private int maxAmount = 1;
    private String displayName;
    private final ArrayList<String> lore = new ArrayList<>();
    private int modelData = -1;
    private final ArrayList<EnchantmentContainer> guaranteedEnchantments = new ArrayList<>();
    private final ArrayList<EnchantmentContainer> bonusEnchantments = new ArrayList<>();
    private int guaranteedEnchantmentsAmount = -1;
    private int bonusEnchantmentsAmount = -1;
    private boolean overrideBonusEnchantments = false;
    private final ArrayList<ItemFlag> flags = new ArrayList<>();
    private int minDurability = -1;
    private int maxDurability = -1;
    private boolean unbreakable;
    private final HashMap<Attribute, AttributeModifier> attributes = new HashMap<>();
    private String skullID;

    public ItemStackContainer(Material material){
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setAmount(int minAmount, int maxAmount){
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public boolean staticAmount(){
        return minAmount == maxAmount;
    }

    public int getMinAmount(){
        return minAmount;
    }

    public int getMaxAmount(){
        return maxAmount;
    }

    public void setDisplayName(String name){
        this.displayName = name;
    }

    public boolean hasDisplayName(){
        return displayName != null;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void addLore(List<String> list){
        lore.addAll(list);
    }

    public void addLore(String line){
        lore.add(line);
    }

    public void skipLine(){
        lore.add("");
    }

    public boolean hasLore(){
        return !lore.isEmpty();
    }

    public ArrayList<String> getLore(){
        return lore;
    }

    public void setModelData(int modelData){
        this.modelData = modelData;
    }

    public boolean hasModelData(){
        return modelData != -1;
    }

    public int getModelData(){
        return modelData;
    }

    public boolean hasGuaranteedEnchantments(){
        return !guaranteedEnchantments.isEmpty();
    }

    public ArrayList<EnchantmentContainer> getGuaranteedEnchantments(){
        return guaranteedEnchantments;
    }

    public void addGuaranteedEnchantment(EnchantmentContainer container){
        this.guaranteedEnchantments.add(container);
    }

    public void setGuaranteedEnchantmentsAmount(int amount){
        this.guaranteedEnchantmentsAmount = amount;
    }

    public boolean hasGuaranteedEnchantmentsAmount(){
        return guaranteedEnchantmentsAmount > -1;
    }

    public int getGuaranteedEnchantmentsAmount(){
        return guaranteedEnchantmentsAmount;
    }

    public boolean hasBonusEnchantments(){
        return !bonusEnchantments.isEmpty();
    }

    public ArrayList<EnchantmentContainer> getBonusEnchantments() {
        return bonusEnchantments;
    }

    public void addBonusEnchantment(EnchantmentContainer container){
        bonusEnchantments.add(container);
    }

    public void setBonusEnchantmentsAmount(int amount){
        this.bonusEnchantmentsAmount = amount;
    }

    public boolean hasBonusEnchantmentsAmount(){
        return bonusEnchantmentsAmount > -1;
    }

    public int getBonusEnchantmentsAmount(){
        return bonusEnchantmentsAmount;
    }

    public boolean overrideBonusEnchantments(){
        return overrideBonusEnchantments;
    }

    public void setOverrideBonusEnchantments(boolean allow){
        this.overrideBonusEnchantments = allow;
    }

    public void addFlags(List<String> flags){
        for(String flag : flags){
            this.flags.add(ItemFlag.valueOf(flag));
        }
    }

    public void addFlag(ItemFlag itemFlag){
        this.flags.add(itemFlag);
    }

    public boolean hasFlags(){
        return !flags.isEmpty();
    }

    public ArrayList<ItemFlag> getFlags(){
        return flags;
    }

    public void setDurability(int min, int max){
        this.minDurability = min;
        this.maxDurability = max;
    }

    public boolean hasDurability(){
        return minDurability != -1 && maxDurability != -1;
    }

    public int getMinDurability(){
        return minDurability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public void setUnbreakable(){
        this.unbreakable = true;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void addAttribute(Attribute attribute, String name, double value, AttributeModifier.Operation operation, EquipmentSlot slot){
        this.attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), name, value, operation, slot));
    }

    public boolean hasAttributes(){
        return !attributes.isEmpty();
    }

    public HashMap<Attribute, AttributeModifier> getAttributes() {
        return attributes;
    }

    public boolean isSkull(){
        return material == Material.PLAYER_HEAD;
    }

    public boolean hasSkullID(){
        return skullID != null && !skullID.isEmpty();
    }

    public String getSkullID(){
        return skullID;
    }


}
