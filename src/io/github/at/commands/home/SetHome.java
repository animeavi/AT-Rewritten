package io.github.at.commands.home;

import io.github.at.config.Config;
import io.github.at.config.CustomMessages;
import io.github.at.config.Homes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SetHome implements CommandExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Config.isFeatureEnabled("homes")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (sender.hasPermission("at.member.sethome")) {
                    if (args.length>0) {
                        if (Bukkit.getPlayer(args[0]) != null) {
                            if (sender.hasPermission("tbh.tp.admin.sethome")) {
                                // We'll just assume that the admin command overrides the homes limit.
                                Player target = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                                setHome(target);
                            }
                        }
                    } else {
                        setHome(player);
                    }
                }
            } else {
                sender.sendMessage(CustomMessages.getString("Error.notAPlayer"));
            }
        } else {
            sender.sendMessage(CustomMessages.getString("Error.featureDisabled"));
            return false;
        }
        return false;
    }

    // Separated this into a separate method so that the code is easier to read.
    // Player player - the player which is having the home set.
    // String name - the name of the home.
    private void setHome(Player player) {
        Location home = player.getLocation();

        try {
            try {
                Homes.setHome(player, home);
                player.sendMessage(CustomMessages.getString("Info.setHome"));
            } catch (IOException e) {
                e.getStackTrace();
            }
        } catch (NullPointerException ex) {
            try {
                Homes.setHome(player, home);
                player.sendMessage(CustomMessages.getString("Info.setHome"));
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }
}
