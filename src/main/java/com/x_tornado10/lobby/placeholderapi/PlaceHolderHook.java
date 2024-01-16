package com.x_tornado10.lobby.placeholderapi;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class PlaceHolderHook extends PlaceholderExpansion {
    private final Database db;
    private final Lobby plugin;
    public PlaceHolderHook() {
        plugin = Lobby.getInstance();
        db = plugin.getDatabase();
    }
    @Override
    public @NotNull String getIdentifier() {
        return "lobby";
    }

    @Override
    public @NotNull String getAuthor() {
        return "x_tornado10";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        PlayerStats stats;
        try {
            stats = db.findPlayerStatsByUUID(String.valueOf(player.getUniqueId()));
        } catch (SQLException e) {
            return super.onRequest(player, params);
        }
        if (stats == null) {
            stats = new PlayerStats(String.valueOf(player.getUniqueId()),0,0,0,0,0,null,0,0,0,0);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return switch (params) {
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "player_kills" -> String.valueOf(stats.getPlayer_kills());
            case "mob_kills" -> String.valueOf(stats.getMob_kills());
            case "blocks_broken" -> String.valueOf(stats.getBlocks_broken());
            case "blocks_placed" -> String.valueOf(stats.getBlocks_placed());
            case "last_login" -> stats.getLast_login() == null ? null : dateFormat.format(stats.getLast_login());
            case "login_streak" -> String.valueOf(stats.getLogin_streak());
            case "logins" -> String.valueOf(stats.getLogins());
            case "chat_messages_send" -> String.valueOf(stats.getChat_messages_send());
            case "playtime" -> formatSeconds(stats.getPlaytime() / 1000);
            case "prefix" -> plugin.getPrefix(player.getUniqueId());
            case "suffix" -> plugin.getSuffix(player.getUniqueId());
            default -> super.onRequest(player, params);
        };
    }

    public static String formatSeconds(long seconds) {
        int d = (int) (seconds / (24 * 3600));
        seconds %= (24 * 3600);
        int h = (int) (seconds / 3600);
        seconds %= 3600;
        int m = (int) (seconds / 60);
        seconds %= 60;
        int s = (int) seconds;

        StringBuilder formattedTime = new StringBuilder();

        if (d > 0) {
            formattedTime.append(d).append("d ");
        }

        if (h > 0) {
            formattedTime.append(h).append("h ");
        }

        if (m > 0) {
            formattedTime.append(m).append("m ");
        }

        if (s > 0 || formattedTime.isEmpty()) {
            formattedTime.append(s).append("s");
        }

        return formattedTime.toString().replaceAll(" $", "");
    }

    public static void registerHook() {
        new PlaceHolderHook().register();
    }
}
