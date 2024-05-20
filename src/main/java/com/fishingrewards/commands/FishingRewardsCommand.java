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

    private ConfigManager configManager;
    private PluginManager pluginManager;

    private String[] help = {"&7/fishingrewards open - Opens a GUI of the loaded rewards.", "&7/fishingrewards mode [weight/roll/treasure] - Changes the Reward Mode and automatically reloads rewards.yml", "&7/fishingrewards reload - Reloads rewards.yml"};
    private String noPerm = "&5Insufficient Permissions.";
    private String notPlayer = "&5You have to be a player to execute this command.";

    public FishingRewardsCommand(FishingRewards plugin){
        this.configManager = plugin.getConfigManager();
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
            if (args.length > 0) {
                switch (args[0]) {
                    case "open":
                        if (sender instanceof Player) {
                            pluginManager.openRewardGUI((Player) sender, 0);
                            return true;
                        }
                        sender.sendMessage(notPlayer);
                        return true;
                    case "mode":
                        if (args.length > 1) {
                            ConfigManager.RewardMode mode = ConfigManager.RewardMode.valueOf(args[1].toUpperCase());
                            configManager.setRewardMode(mode);
                            pluginManager.reload();
                            return true;
                        }
                        sender.sendMessage(ChatColor.GRAY + "Current mode is '" + configManager.getRewardMode().toString() + "'");
                        return true;
                    case "reload":
                        pluginManager.reload();
                        return true;
                }
            } else {
                for (String s : help) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
                return true;
            }
        }
        return true;
    }

}
