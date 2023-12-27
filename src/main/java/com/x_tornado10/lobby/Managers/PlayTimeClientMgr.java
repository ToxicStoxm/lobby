package com.x_tornado10.lobby.Managers;

import com.x_tornado10.lobby.Lobby;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeClientMgr {
    private final Lobby plugin;
    private HashMap<UUID, Double> playtime;
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
        }.runTaskTimer(plugin, 100, 100);
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
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(entry.getKey());
            plugin.sendCustomData(p, entry.getKey(),entry.getValue(),"playtime");
        }
    }
}
