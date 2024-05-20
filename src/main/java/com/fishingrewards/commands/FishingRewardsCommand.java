package com.fishingrewards.commands;

import com.fishingrewards.ConfigManager;
import com.fishingrewards.FishingRewards;
import com.fishingrewards.PluginManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FishingRewardsCommand implements CommandExecutor {

    private PluginManager pluginManager;

    private String[] help = {"&7/fishingrewards gui - Opens a GUI of the loaded rewards.", "&7/fishingrewards reload - Reloads config and rewards files."};
    private String noPerm = "&5Insufficient Permissions.";
    private String notPlayer = "&5You have to be a player to execute this command.";

    public FishingRewardsCommand(FishingRewards plugin){
        this.pluginManager = plugin.getPluginManager();
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
                     sender.sendMessage(help);
                     return true;
                 case 1:
                     if(args[0].equalsIgnoreCase("gui")){
                         if(sender instanceof Player player){
                             pluginManager.openRewardGUI(player, 0);
                         }else{
                             sender.sendMessage(notPlayer);
                         }
                         return true;
                     }
                     if(args[0].equalsIgnoreCase("reload")){
                         pluginManager.reload();
                         return true;
                     }
             }
        }
        return true;
    }

}
