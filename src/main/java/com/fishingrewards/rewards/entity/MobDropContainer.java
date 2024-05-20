package com.fishingrewards.rewards.entity;

import com.fishingrewards.rewards.item.ItemStackContainer;


public class MobDropContainer {

    private ItemStackContainer itemStackContainer;
    private double chance;
    private boolean damaged = false;

    public MobDropContainer(ItemStackContainer itemStackContainer, double chance){
        this.itemStackContainer = itemStackContainer;
        this.chance = chance;
    }

    public MobDropContainer(ItemStackContainer itemStackContainer, double chance, boolean damaged){
        this.itemStackContainer = itemStackContainer;
        this.chance = chance;
        this.damaged = damaged;
    }

    public ItemStackContainer getItemStackContainer() {
        return itemStackContainer;
    }

    public double getChance() {
        return chance;
    }

    public boolean isDamaged(){
        return damaged;
    }

}
