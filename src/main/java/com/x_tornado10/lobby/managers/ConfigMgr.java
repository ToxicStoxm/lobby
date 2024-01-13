package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.statics.Paths;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public Location spawn() {
        return new Location(Bukkit.getWorld(Objects.requireNonNull(config.getString(Paths.world))), config.getDouble(Paths.x), config.getDouble(Paths.y), config.getDouble(Paths.z), (float) config.getDouble(Paths.yaw), (float) config.getDouble(Paths.pitch));
    }
    public String joinMsg() {
        return config.getString(Paths.join_msg);
    }
    public List<String> getDbCredentials() {
        List<String> result = new ArrayList<>();
        result.add(config.getString(Paths.db_host));
        result.add(config.getString(Paths.db_username));
        result.add(config.getString(Paths.db_password));
        return  result;
    }
    public List<Location> getDoor() {
        List<Location> locs = new ArrayList<>();
        World w = Bukkit.getWorld(Objects.requireNonNull(config.getString(Paths.door_world)));
        Location loc1 = new Location(w,config.getDouble(Paths.door_1_x),config.getDouble(Paths.door_1_y), config.getDouble(Paths.door_1_z)).getBlock().getLocation();
        Location loc2 = new Location(w,config.getDouble(Paths.door_2_x),config.getDouble(Paths.door_2_y), config.getDouble(Paths.door_2_z)).getBlock().getLocation();
        int x_min = (int) Math.min(loc1.getX(), loc2.getX());
        int y_min = (int) Math.min(loc1.getY(), loc2.getY());
        int z_min = (int) Math.min(loc1.getZ(), loc2.getZ());

        int x_max = (int) Math.max(loc1.getX(), loc2.getX());
        int y_max = (int) Math.max(loc1.getY(), loc2.getY());
        int z_max = (int) Math.max(loc1.getZ(), loc2.getZ());

        for (int i = x_min; i <= x_max; i++) {
            for (int j = y_min; j <= y_max; j++) {
                for (int k = z_min; k <= z_max; k++) {
                    locs.add(new Location(w,i,j,k));
                }
            }
        }
        return locs;
    }
    public FileConfiguration getMilestones() throws NullPointerException {
        InputStream inputStream = plugin.getResource("milestones.yml");
        if (inputStream == null) throw new NullPointerException();
        return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
    }
}
