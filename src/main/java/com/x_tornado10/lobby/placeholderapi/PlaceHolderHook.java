package com.x_tornado10.lobby.placeholderapi;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class PlaceHolderHook extends PlaceholderExpansion {
    private final Lobby plugin;
    private final Database db;
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
        if (stats == null) return super.onRequest(player, params);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return switch (params) {
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "player_kills" -> String.valueOf(stats.getPlayer_kills());
            case "mob_kills" -> String.valueOf(stats.getMob_kills());
            case "blocks_broken" -> String.valueOf(stats.getBlocks_broken());
            case "blocks_placed" -> String.valueOf(stats.getBlocks_placed());
            case "last_login" -> dateFormat.format(stats.getLast_login());
            case "login_streak" -> String.valueOf(stats.getLogin_streak());
            case "logins" -> String.valueOf(stats.getLogins());
            case "chat_messages_send" -> String.valueOf(stats.getChat_messages_send());
            case "playtime" -> String.valueOf(stats.getPlayer_kills());
            default -> super.onRequest(player, params);
        };
    }


    public static void registerHook() {
        new PlaceHolderHook().register();
    }
}
