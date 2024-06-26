package com.x_tornado10.lobby.updateLeaderboard;

import com.x_tornado10.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateLeaderboard {
    private final int delay;
    private final int cycle;
    private final String[] commands;

    public UpdateLeaderboard(int delay, int cycle, String[] commands) {
        this.delay = delay;
        this.cycle = cycle;
        this.commands = commands;
        update();
    }

    private void update() {
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final Server server = Bukkit.getServer();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String s : commands) server.dispatchCommand(console, s);
            }
        }.runTaskTimer(Lobby.getInstance(), delay, cycle);

    }

}
