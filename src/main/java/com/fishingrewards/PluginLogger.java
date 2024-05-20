package com.fishingrewards;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginLogger {

	private FishingRewards plugin;
	private Logger logger;

	public PluginLogger(FishingRewards plugin){
		this.plugin = plugin;
		this.logger = plugin.getLogger();
	}

	public void log(String message){
		logger.log(Level.INFO, message);
	}

	public void error(String error, boolean shutdown){
		logger.log(Level.INFO, "[Error] " + error);
		if(shutdown) plugin.getPluginLoader().disablePlugin(plugin);
	}

	public void rewardError(String reward, String tag){
		 error("loading reward '" + reward + "'. Reward needs a valid '" + tag + "'", false);
	}

}
