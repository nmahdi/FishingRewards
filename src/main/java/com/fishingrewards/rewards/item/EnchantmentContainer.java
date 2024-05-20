package com.fishingrewards.rewards.item;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentContainer {

	private Enchantment enchantment;
	private int minLevel, maxLevel;
	private double chance;

	public EnchantmentContainer(Enchantment enchantment, int minLevel, int maxLevel, double chance){
		this.enchantment = enchantment;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.chance = chance;
	}

	public Enchantment getEnchantment() {
		return enchantment;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public double getChance() {
		return chance;
	}

	public boolean isGuaranteed(){
		return chance == 100;
	}

}
