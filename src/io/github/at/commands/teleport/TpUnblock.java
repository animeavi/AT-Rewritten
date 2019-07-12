package io.github.at.commands.teleport;

import io.github.at.config.Config;
import io.github.at.config.TpBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class TpUnblock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (Config.featTP()) {
            if (sender.hasPermission("tbh.tp.member.unblock")) {
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    if (args.length>0){
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "You've never blocked yourself.");
                            return false;
                        }
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                        if (target == null){
                            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " The player doesn't exist.");
                            return false;
                        } else {
                            if (TpBlock.getBlockedPlayers(player).contains(target.getPlayer())){
                                try {
                                    TpBlock.remBlockedPlayer(player, target.getPlayer());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sender.sendMessage(ChatColor.GREEN + "Successfully unblocked " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " .");
                                return false;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR:" + ChatColor.RED + " You must include a player name!");
                        return false;
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED + "The feature " + ChatColor.GOLD + "Teleport " + ChatColor.RED + "is disabled!");
            return false;
        }
        return false;
    }
}
