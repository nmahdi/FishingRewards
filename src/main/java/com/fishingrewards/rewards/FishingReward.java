package com.fishingrewards.rewards;

import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;

public class FishingReward implements Comparable<FishingReward> {

	protected String name;
	protected String catchMessage;
	protected ArrayList<String> commandsRan = new ArrayList<>();
	protected int minXP, maxXP = 0;
	protected double chance = 0;
	protected List<String> regions;
	protected List<Biome> biomes;
	protected String permission;
	protected int[] time;
	protected boolean[] weather = new boolean[3];
	protected boolean treasure = false;
	protected boolean junk = false;

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setCatchMessage(String catchMessage){
		this.catchMessage = catchMessage;
	}

	public String getCatchMessage() {
		return catchMessage;
	}

	public void setCommandsRan(List<String> commandsRan) {
		this.commandsRan.addAll(commandsRan);
	}

	public ArrayList<String> getCommandsRan() {
		return commandsRan;
	}

	public void setXP(int[] xp){
		this.minXP = xp[0];
		this.maxXP = xp[1];
	}

	public int getMinXP() {
		return minXP;
	}

	public int getMaxXP() {
		return maxXP;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public double getChance() {
		return chance;
	}

	public void addRegionCondition(List<String> regions){
		this.regions = regions;
	}

	public List<String> getRegionCondition(){
        return regions;
	}

	public boolean hasRegionCondition(){
		return regions != null && !regions.isEmpty();
	}

	public void addBiomeCondition(List<Biome> biomes) {
		this.biomes = biomes;
	}

	public List<Biome> getBiomeCondition(){
		return biomes;
	}

	public boolean hasBiomeConditions(){
		return biomes != null && !biomes.isEmpty();
	}

	public void addPermissionCondition(String permission){
		this.permission = permission;
	}

	public String getPermissionCondition(){
		return permission;
	}

	public boolean hasPermissionCondition(){
		return permission != null && !permission.isEmpty();
	}


	public boolean hasClearWeatherConditon(){
		return weather[WeatherCondition.CLEAR.getId()];
	}

	public boolean hasRainWeatherCondition(){
		return weather[WeatherCondition.RAIN.getId()];
	}

	public boolean hasThunderCondition(){
		return weather[WeatherCondition.THUNDER.getId()];
	}

	public void setWeatherConditon(WeatherCondition condition){
		weather[condition.getId()] = true;
	}

	public void addTimeCondition(int[] time){
		this.time = time;
	}

	public int[] getTimeCondition(){
		return time;
	}

	public boolean hasTimeCondition(){
		return time != null && time.length > 1;
	}

	public boolean isTreasure() {
		return treasure;
	}

	public void setTreasure() {
		this.treasure = true;
	}

	public boolean isJunk(){
		return junk;
	}

	public void setJunk(){
		this.junk = true;
	}

	@Override
	public int compareTo(FishingReward reward) {
		return Double.compare(chance, reward.chance);
	}
}
