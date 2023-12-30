package com.x_tornado10.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tchristofferson.configupdater.ConfigUpdater;
import com.x_tornado10.lobby.commands.LobbyCommand;
import com.x_tornado10.lobby.commands.LobbyCommandDisabled;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.listeners.CompassListener;
import com.x_tornado10.lobby.listeners.JoinListener;
import com.x_tornado10.lobby.listeners.LobbyListener;
import com.x_tornado10.lobby.listeners.PlayerStatsListener;
import com.x_tornado10.lobby.managers.ConfigMgr;
import com.x_tornado10.lobby.managers.LobbyCompass;
import com.x_tornado10.lobby.utils.Paths;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;


public final class Lobby extends JavaPlugin {

    public static Lobby instance;

    public static boolean isLobby;
    public static LobbyCompass lobbyCompass;

    public static Lobby getInstance() {
        return instance;
    }
    private ConfigMgr configMgr;

    public ConfigMgr getConfigMgr() {
        return configMgr;
    }
    private JoinListener joinListener;
    private Database database;
    private Connection connection;
    private Logger logger;
    private LuckPerms lpAPI;

    public LuckPerms getLpAPI() {
        return lpAPI;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = getLogger();
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            logger.severe("Error while trying to update config.yml!");
            logger.severe("If this error persists after restarting the server please file a bug report!");
        }
        reloadConfig();
        Paths.initialize();
        configMgr = new ConfigMgr();
        database = new Database(configMgr.getDbCredentials());

        try {
            logger.info(configMgr.getDbCredentials().toString());
            connection = database.getConnection();
            logger.info("Successfully established connection to MySQL database.");
        } catch (SQLException e) {
            logger.severe("Connection to MySQL database could not be established. Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            database.initialize();
            logger.info("Successfully initialized database.");
        } catch (SQLException e) {
            logger.severe("Couldn't initialize database! Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        lobbyCompass = new LobbyCompass();
        isLobby = configMgr.isLobby();
        joinListener = new JoinListener(configMgr.spawn(), configMgr.joinMsg());
        if (isLobby) {
            lpAPI = LuckPermsProvider.get();
            Bukkit.getPluginManager().registerEvents(joinListener, this);
            Bukkit.getPluginManager().registerEvents(new LobbyListener(configMgr.isBuildMode()), this);
            PluginCommand lobby = Bukkit.getPluginCommand("lobby");
            if (lobby != null) {
                lobby.setExecutor(new LobbyCommandDisabled());
            }
        } else {
            PluginCommand lobby = Bukkit.getPluginCommand("lobby");
            if (lobby != null) {
                lobby.setExecutor(new LobbyCommand());
            }
        }
        Bukkit.getPluginManager().registerEvents(new PlayerStatsListener(), this);
        Bukkit.getPluginManager().registerEvents(new CompassListener(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (database != null) {
            database.closeConnectionPool();
        } else {
            logger.severe("Couldn't close connection to database.");
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public Database getDatabase() {
        return database;
    }

    public Connection getConnection() {
        return connection;
    }
}
