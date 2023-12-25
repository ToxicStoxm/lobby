package com.x_tornado10.lobby.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LobbyCommandDisabled implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (!(commandSender instanceof Player )) {
            commandSender.sendMessage("Only Players can execute this command!");
            return true;
        }
        return false;
    }
}
