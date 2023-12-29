package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.managers.PlayTimeClientMgr;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeListener implements Listener {
    private final Lobby plugin;
    private final PlayTimeClientMgr playTimeClientMgr;
    private final HashMap<UUID, Double> playtime;
    public PlayTimeListener() {
        plugin = Lobby.getInstance();
        playTimeClientMgr = plugin.getPlayTimeClientMgr();
        playtime = new HashMap<>();
        updateLoop();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        playtime.put(e.getPlayer().getUniqueId(), (double) System.currentTimeMillis());
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        playtime.remove(e.getPlayer().getUniqueId());
    }
    public void updateLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Map.Entry<UUID, Double> entry : playtime.entrySet()) {
                    playTimeClientMgr.updatePlaytime(entry.getKey(), convert(entry.getValue()));
                    playtime.put(entry.getKey(), (double) System.currentTimeMillis());
                }

            }
        }.runTaskTimer(plugin,100,100);
    }
    public double convert(double millis) {
        return Math.round((System.currentTimeMillis() - millis) / 1000);
    }
}
