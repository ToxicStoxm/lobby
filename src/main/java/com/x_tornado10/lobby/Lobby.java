package com.x_tornado10.lobby;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.x_tornado10.lobby.Managers.ConfigMgr;
import com.x_tornado10.lobby.Managers.LobbyCompass;
import com.x_tornado10.lobby.command.LobbyCommand;
import com.x_tornado10.lobby.command.LobbyCommandDisabled;
import com.x_tornado10.lobby.listeners.CompassListener;
import com.x_tornado10.lobby.listeners.JoinListener;
import com.x_tornado10.lobby.listeners.LobbyListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;


public final class Lobby extends JavaPlugin implements PluginMessageListener {

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

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        configMgr = new ConfigMgr();
        lobbyCompass = new LobbyCompass();
        isLobby = configMgr.isLobby();
        if (isLobby) {
            Bukkit.getPluginManager().registerEvents(new JoinListener(configMgr.spawn(), configMgr.joinMsg()), this);
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
        Bukkit.getPluginManager().registerEvents(new CompassListener(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, @NonNull Player player, byte @NonNull [] message) {
        if (!channel.equals("BungeeCord") && configMgr.isLobby()) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("SomeSubChannel")) {
            // Use the code sample in the 'Response' sections below to read
            // the data.
        }
    }
    public void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName); // The name of the server you want to send the player to

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
