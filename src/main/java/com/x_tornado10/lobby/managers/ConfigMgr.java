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
    public List<String> getDbCredentials() {
        List<String> result = new ArrayList<>();
        result.add(config.getString(Paths.db_host));
        result.add(config.getString(Paths.db_username));
        result.add(config.getString(Paths.db_password));
        return  result;
    }
    public FileConfiguration getMilestones() throws NullPointerException {
        InputStream inputStream = plugin.getResource("milestones.yml");
        if (inputStream == null) throw new NullPointerException();
        return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
    }
}
