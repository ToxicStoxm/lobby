package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeClientMgr {
    private final Lobby plugin;
    private final HashMap<UUID, Double> playtime;
    public PlayTimeClientMgr() {
        plugin = Lobby.getInstance();
        playtime = new HashMap<>();
        saveLoop();
    }
    private void saveLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(plugin, 0, 100);
    }
    public void updatePlaytime(UUID pid, double seconds) {
        if (playtime.containsKey(pid)) {
            playtime.put(pid, playtime.get(pid) + seconds);
        } else {
            playtime.put(pid, seconds);
        }
    }
    private void save() {
        for (Map.Entry<UUID, Double> entry : playtime.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null) {
                //plugin.sendCustomData(p, entry.getKey(), entry.getValue(), "playtime");
            }
            playtime.remove(entry.getKey());
        }
    }
}
