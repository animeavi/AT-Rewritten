package io.github.at.commands.teleport;

import io.github.at.config.Config;
import io.github.at.config.CustomMessages;
import io.github.at.config.TpBlock;
import io.github.at.events.CooldownManager;
import io.github.at.main.Main;
import io.github.at.utilities.DistanceLimiter;
import io.github.at.utilities.PaymentManager;
import io.github.at.utilities.TPRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Config.isFeatureEnabled("teleport")) {
                if (sender.hasPermission("at.member.tpa")) {
                    if (CooldownManager.getCooldown().containsKey(player)) {
                        sender.sendMessage(CustomMessages.getString("Error.onCooldown").replaceAll("\\{time}", String.valueOf(Config.commandCooldown())));
                        return false;
                    }
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase(player.getName())){
                            sender.sendMessage(CustomMessages.getString("Error.requestSentToSelf"));
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(CustomMessages.getString("Error.noSuchPlayer"));
                            return false;
                        } else {
                            if (TpOff.getTpOff().contains(target)) {
                                sender.sendMessage(CustomMessages.getString("Error.tpOff").replaceAll("\\{player}", target.getName()));
                                return false;
                            }
                            if (TpBlock.getBlockedPlayers(target).contains(player)) {
                                sender.sendMessage(CustomMessages.getString("Error.tpBlock").replaceAll("\\{player}", target.getName()));
                                return false;
                            }
                            if (TPRequest.getRequestByReqAndResponder(target, player) != null) {
                                sender.sendMessage(CustomMessages.getString("Error.alreadySentRequest").replaceAll("\\{player}", target.getName()));
                                return false;
                            }
                            if (!DistanceLimiter.canTeleport(player.getLocation(), target.getLocation(), "tpa") && !player.hasPermission("at.admin.bypass.distance-limit")) {
                                player.sendMessage(CustomMessages.getString("Error.tooFarAway"));
                                return false;
                            }
                            if (PaymentManager.canPay("tpa", player)) {
                                sender.sendMessage(CustomMessages.getString("Info.requestSent")
                                        .replaceAll("\\{player}", target.getName())
                                        .replaceAll("\\{lifetime}", String.valueOf(Config.requestLifetime())));
                                target.sendMessage(CustomMessages.getString("Info.tpaRequestReceived")
                                        .replaceAll("\\{player}", sender.getName())
                                        .replaceAll("\\{lifetime}", String.valueOf(Config.requestLifetime())));
                                BukkitRunnable run = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        sender.sendMessage(CustomMessages.getString("Error.requestExpired").replaceAll("\\{player}", target.getName()));
                                        TPRequest.removeRequest(TPRequest.getRequestByReqAndResponder(target, player));
                                    }
                                };
                                run.runTaskLater(Main.getInstance(), Config.requestLifetime()*20); // 60 seconds
                                TPRequest request = new TPRequest(player, target, run, TPRequest.TeleportType.TPA_NORMAL); // Creates a new teleport request.
                                TPRequest.addRequest(request);
                                BukkitRunnable cooldowntimer = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        CooldownManager.getCooldown().remove(player);
                                    }
                                };
                                CooldownManager.getCooldown().put(player, cooldowntimer);
                                cooldowntimer.runTaskLater(Main.getInstance(), Config.commandCooldown()*20); // 20 ticks = 1 second
                                return false;
                            }

                        }
                    } else {
                        sender.sendMessage(CustomMessages.getString("Error.noPlayerInput"));
                        return false;
                    }
                }
                return false;
            }
        } else {
            sender.sendMessage(CustomMessages.getString("Error.notAPlayer"));
        }
        return false;
    }
}
