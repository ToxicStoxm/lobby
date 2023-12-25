package com.x_tornado10.lobby.Managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Paths;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class ConfigMgr {
    private final FileConfiguration config;
    public ConfigMgr() {
        Lobby plugin = Lobby.getInstance();
        config = plugin.getConfig();
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
}
