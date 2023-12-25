package com.x_tornado10.lobby.command;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LobbyCommand implements CommandExecutor {
    private final Lobby plugin;
    public LobbyCommand() {
        plugin = Lobby.getInstance();
    }
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Only Players can execute this command!");
            return true;
        }
        plugin.sendPlayerToServer(p, Server.Lobby);
        return true;
    }
}
