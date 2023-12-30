package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Paths;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class ConfigMgr {
    private final FileConfiguration config;
    private final Lobby plugin;
    public ConfigMgr() {
        plugin = Lobby.getInstance();
        config = plugin.getConfig();
        checkFiles();
    }

    private void checkFiles() {
        for (String s : Paths.plFiles) {
            File file = new File(plugin.getDataFolder(),s);
            if (!file.exists()) {
                plugin.saveResource(s, false);
            }
        }
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
    public List<String> getDbCredentials() {
        List<String> result = new ArrayList<>();
        result.add(config.getString(Paths.db_host));
        result.add(config.getString(Paths.db_username));
        result.add(config.getString(Paths.db_password));
        return  result;
    }
}
