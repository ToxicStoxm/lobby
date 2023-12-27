package com.x_tornado10.lobby.Managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Paths;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class ConfigMgr {
    private final FileConfiguration config;
    private final Lobby plugin;
    public ConfigMgr() {
        plugin = Lobby.getInstance();
        config = plugin.getConfig();
        if(checkFiles()) {
            throw new RuntimeException();
        }
    }

    private boolean checkFiles() {
        boolean err = false;
        for (String s : Paths.plFiles) {
            File file = new File(plugin.getDataFolder(),s);
            if (!file.exists()) {
                plugin.saveResource(s, false);
            }
        }
        return err;
    }

    public boolean isLobby() {
        return config.getBoolean(Paths.lobby);
    }
    public Location spawn() {
        return new Location(Bukkit.getWorld(Objects.requireNonNull(config.getString(Paths.world))), config.getDouble(Paths.x), config.getDouble(Paths.y), config.getDouble(Paths.z), (float) config.getDouble(Paths.yaw), (float) config.getDouble(Paths.pitch));
    }
    public String joinMsg() {
        return config.getString(Paths.join_msg);
    }
    public boolean isBuildMode() {
        return config.getBoolean(Paths.build_mode);
    }
    public void saveJoinCounter(HashMap<UUID, Integer> join_counter) {
        ConfigurationSection sec = config.getConfigurationSection("Join-Counter");
        if (sec == null) return;
        for (Map.Entry<UUID, Integer> entry : join_counter.entrySet()) {
            sec.set(String.valueOf(entry.getKey()), entry.getValue());
        }
    }
    public HashMap<UUID, Integer> getJoinCounter() {
        ConfigurationSection sec = config.getConfigurationSection("Join-Counter");
        if (sec == null) return null;
        HashMap<UUID, Integer> temp = new HashMap<>();
        for (String s : sec.getKeys(false)) {
            try {
                temp.put(UUID.fromString(s), sec.getInt(s));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return temp;
    }
}
