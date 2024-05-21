package com.fishingrewards.rewards.entity;

import com.fishingrewards.rewards.FishingReward;
import com.fishingrewards.rewards.item.ItemStackContainer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;

public class FishingEntityReward extends FishingReward {

    private EntityType entityType;
    private HashMap<Attribute, Double> attributes = new HashMap<>();
    private ArrayList<MobDropContainer> drops = new ArrayList<>();
    private HashMap<EquipmentSlot, ItemStackContainer> equipment = new HashMap<>();

    public FishingEntityReward(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean hasAttributes(){
        return !attributes.isEmpty();
    }

    public boolean hasAttribute(Attribute attribute){
        return attributes.containsKey(attribute);
    }

    public void setAttributes(HashMap<Attribute, Double> attributes){
        this.attributes = attributes;
    }

    public double getAttribute(Attribute attribute){
        return attributes.get(attribute);
    }

    public boolean hasDrops(){
        return !drops.isEmpty();
    }

    public void setDrops(ArrayList<MobDropContainer> drops){
        this.drops = drops;
    }

    public void addDrop(MobDropContainer drop){
        this.drops.add(drop);
    }

    public ArrayList<MobDropContainer> getDrops(){
        return drops;
    }

    public void setEquipment(HashMap<EquipmentSlot, ItemStackContainer> equipment){
        this.equipment = equipment;
    }

    public ItemStackContainer getEquipment(EquipmentSlot slot){
        return equipment.get(slot);
    }

    public ItemStackContainer getHelmet(){
        return equipment.get(EquipmentSlot.HEAD);
    }

    public boolean hasHelmet(){
        return getHelmet() != null;
    }

    public ItemStackContainer getChestplate(){
        return equipment.get(EquipmentSlot.CHEST);
    }

    public boolean hasChestplate(){
        return getChestplate() != null;
    }

    public ItemStackContainer getLeggings(){
        return equipment.get(EquipmentSlot.LEGS);
    }

    public boolean hasLeggings(){
        return getLeggings() != null;
    }
    public ItemStackContainer getBoots(){
        return equipment.get(EquipmentSlot.FEET);
    }

    public boolean hasBoots(){
        return getBoots() != null;
    }

    public ItemStackContainer getHand(){
        return equipment.get(EquipmentSlot.HAND);
    }

    public boolean hasHand(){
        return getHand() != null;
    }
    public ItemStackContainer getOffhand(){
        return equipment.get(EquipmentSlot.OFF_HAND);
    }

    public boolean hasOffhand(){
        return getOffhand() != null;
    }

}
