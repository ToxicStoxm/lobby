package com.x_tornado10.lobby.Managers;

import com.x_tornado10.lobby.Lobby;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayTimeMainMgr {
    private final Lobby plugin;
    private final FileConfiguration playtime;
    private final File file;
    public PlayTimeMainMgr() {
        plugin = Lobby.getInstance();
        file = new File(plugin.getDataFolder(), "playtime.yml");
        playtime = new YamlConfiguration();
        try {
            playtime.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
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
        if (playtime.contains(String.valueOf(pid))) {
            playtime.set(String.valueOf(pid), playtime.getDouble(String.valueOf(pid)) + seconds);
        } else {
            playtime.set(String.valueOf(pid), seconds);
        }
    }
    private void save() {
        try {
            playtime.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
