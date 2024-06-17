package com.fishingrewards.commands;

import com.fishingrewards.*;
import com.fishingrewards.managers.ConfigManager;
import com.fishingrewards.managers.GUIHandler;
import com.fishingrewards.managers.RewardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FishingRewardsCommand implements CommandExecutor {

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final RewardManager rewardManager;
    private final GUIHandler guiHandler;

    private final String[] help = {"&7/fishingrewards gui - Opens a GUI of the loaded rewards.", "&7/fishingrewards reload - Reloads config and rewards files."};
    private final String noPerm = ChatColor.translateAlternateColorCodes('&', "&5Insufficient Permissions.");
    private final String notPlayer = ChatColor.translateAlternateColorCodes('&', "&5You have to be a player to execute this command.");
    private final String reloaded = ChatColor.GREEN + "Config & Rewards have been reloaded.";

    public FishingRewardsCommand(FishingRewards plugin){
        this.logger = plugin.getFishingLogger();
        this.configManager = plugin.getConfigManager();
        this.rewardManager = plugin.getRewardManager();
        this.guiHandler = plugin.getGuiHandler();
        plugin.getCommand("fishingrewards").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("fishingrewards")) {
            if(!sender.hasPermission("fishingrewards.admin")) {
                sender.sendMessage(noPerm);
                return true;
            }
             switch(args.length){
                 case 0:
                     sendHelpMessage(sender);
                     return true;
                 case 1:
                     if(args[0].equalsIgnoreCase("gui")){
                         if(sender instanceof Player player){
                             guiHandler.openRewardGUI(player, 0);
                         }else{
                             sender.sendMessage(notPlayer);
                         }
                         return true;
                     }
                     if(args[0].equalsIgnoreCase("reload")){
                         rewardManager.reload();
                         configManager.loadConfig();
                         guiHandler.load();
                         logger.log(reloaded);
                         sender.sendMessage(reloaded);
                         return true;
                     }
             }
            sendHelpMessage(sender);
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender){
        for (String s : help) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

}
