package com.x_tornado10.lobby.commands;

import com.x_tornado10.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class GrantRankCommand implements CommandExecutor {
    private final Lobby plugin;
    private final Logger logger;
    public GrantRankCommand() {
        plugin = Lobby.getInstance();
        logger = plugin.getLogger();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player p) {
            if (plugin.hasPermission(p, "lobby.admin")) {
                if (args.length == 2) {
                    Player pl = Bukkit.getPlayer(args[0]);
                    if (pl == null) {
                        p.sendMessage("Invalid Player!");
                        return true;
                    }
                    p.sendMessage(pl.getName() + " now inherits group " + args[1]);
                    plugin.setPlayerGroup(pl, args[1]);
                } else {
                    p.sendMessage("Wrong syntax!");
                    return true;
                }
            } else {
                p.sendMessage("You don't have the permissions to execute this command!");
                return true;
            }
        } else {
            if (args.length == 2) {
                Player pl = Bukkit.getPlayer(args[0]);
                if (pl == null) {
                    logger.info("Invalid Player!");
                    return true;
                }
                logger.info(pl.getName() + " now inherits group " + args[1]);
                plugin.setPlayerGroup(pl, args[1]);
                if (plugin.hasPremium(pl)) pl.setAllowFlight(true);
            } else {
                logger.info("Wrong syntax!");
                return true;
            }
        }
        return true;
    }
}
