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
import com.x_tornado10.lobby.utils.Paths;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;


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
    private JoinListener joinListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        Paths.initialize();
        configMgr = new ConfigMgr();
        lobbyCompass = new LobbyCompass();
        isLobby = configMgr.isLobby();
        joinListener = new JoinListener(configMgr.spawn(), configMgr.joinMsg());
        if (isLobby) {
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
        Bukkit.getPluginManager().registerEvents(new CompassListener(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        joinListener.saveJoinCounter();
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] bytes) {
        if (!channel.equalsIgnoreCase( "lobby:lobby")) return;
        if (!isLobby) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase( "playtime" )) {
            UUID data1 = UUID.fromString(in.readUTF());
            double data2 = in.readDouble();
        }
    }

    public void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
    public void sendCustomData(ProxiedPlayer player, UUID data1, double data2, String subChannel) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        if ( networkPlayers == null || networkPlayers.isEmpty() ) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(String.valueOf(data1));
        out.writeDouble(data2);
        player.getServer().getInfo().sendData( "lobby:lobby", out.toByteArray() );
    }
}
