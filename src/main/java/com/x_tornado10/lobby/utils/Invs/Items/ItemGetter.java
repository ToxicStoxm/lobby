package com.x_tornado10.lobby.utils.Invs.Items;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemGetter {
    private final Lobby plugin;
    public ItemGetter() {
        plugin = Lobby.getInstance();
    }
    @Nullable
    public List<ItemStack> getStats(@NotNull Player p) {
        List<ItemStack> statsList = new ArrayList<>();
        PlayerStats stats;
        try {
            stats = plugin.getDatabase().findPlayerStatsByUUID(String.valueOf(p.getUniqueId()));
        } catch (SQLException e) {
            return null;
        }
        statsList.add(STATS_PVP(stats));
        statsList.add(STATS_BLOCKS(stats));
        statsList.add(STATS_TIME(stats));
        statsList.add(STATS_MISC(stats));
        return statsList;
    }
    public ItemStack STATS_PVP(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.IRON_SWORD)
                .name(ChatColor.GREEN + "PvP")
                .lore(
                        ChatColor.GRAY + "Player Kills: " + ChatColor.GREEN + stats.getPlayer_kills(),
                        ChatColor.GRAY + "Mob Kills: " + ChatColor.GREEN + stats.getMob_kills(),
                        ChatColor.GRAY + "Deaths: " + ChatColor.RED + stats.getDeaths()
                )
                .make();
    }
    public ItemStack STATS_BLOCKS(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                .name(ChatColor.GREEN + "Building")
                .lore(
                        ChatColor.GRAY + "Blocks Placed: " + ChatColor.GREEN + stats.getBlocks_placed(),
                        ChatColor.GRAY + "Blocks Broken: " + ChatColor.GREEN + stats.getBlocks_broken()
                )
                .make();
    }
    public ItemStack STATS_TIME(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.CLOCK)
                .name(ChatColor.GREEN + "Playtime")
                .lore(
                        ChatColor.GRAY + "Playtime: " + ChatColor.GREEN + formatMillis(stats.getPlaytime()),
                        ChatColor.GRAY + "Last Login: " + ChatColor.GREEN + stats.getLast_login()
                        )
                .make();
    }
    public ItemStack STATS_MISC(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.PAPER)
                .name(ChatColor.GREEN + "Misc")
                .lore(
                        ChatColor.GRAY + "Logins: " + ChatColor.GREEN + stats.getLogins(),
                        ChatColor.GRAY + "Login Streak: " + ChatColor.GREEN + stats.getLogin_streak(),
                        ChatColor.GRAY + "Chat Messages Send: " + ChatColor.GREEN + stats.getChat_messages_send()
                        )
                .make();
    }
    public static String formatMillis(long milliseconds) {
        long seconds = milliseconds / 1000;

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
}
