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
import java.text.SimpleDateFormat;
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
                        ChatColor.GRAY + "Deaths: " + ChatColor.GREEN + stats.getDeaths()
                )
                .hideTags(true)
                .make();
    }
    public ItemStack STATS_BLOCKS(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                .name(ChatColor.GREEN + "Building")
                .lore(
                        ChatColor.GRAY + "Blocks Placed: " + ChatColor.GREEN + stats.getBlocks_placed(),
                        ChatColor.GRAY + "Blocks Broken: " + ChatColor.GREEN + stats.getBlocks_broken()
                )
                .hideTags(true)
                .make();
    }
    public ItemStack STATS_TIME(@NotNull PlayerStats stats) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return ItemCreator.of(CompMaterial.CLOCK)
                .name(ChatColor.GREEN + "Playtime")
                .lore(
                        ChatColor.GRAY + "Playtime: " + ChatColor.GREEN + formatMillis(stats.getPlaytime()),
                        ChatColor.GRAY + "Last Login: " + ChatColor.GREEN + dateFormat.format(stats.getLast_login()),
                        ChatColor.GRAY + "Logins: " + ChatColor.GREEN + stats.getLogins(),
                        ChatColor.GRAY + "Login Streak: " + ChatColor.GREEN + stats.getLogin_streak()
                        )
                .hideTags(true)
                .make();
    }
    public ItemStack STATS_MISC(@NotNull PlayerStats stats) {
        return ItemCreator.of(CompMaterial.PAPER)
                .name(ChatColor.GREEN + "Chat")
                .lore(
                        ChatColor.GRAY + "Chat Messages Send: " + ChatColor.GREEN + stats.getChat_messages_send()
                        )
                .hideTags(true)
                .make();
    }
    public ItemStack CLOSE_BUTTON() {
        return ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE)
                .name(ChatColor.RED + "Close")
                .lore(ChatColor.GRAY + "Close this menu")
                .hideTags(true)
                .make();
    }
    public List<ItemStack> getLocked() {
        List<ItemStack> result = new ArrayList<>();
        result.add(MILESTONE_1_LOCKED());
        result.add(MILESTONE_4_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_5_LOCKED());
        result.add(MILESTONE_8_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_2_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_3_LOCKED());
        result.add(MILESTONE_6_LOCKED());
        result.add(MILESTONE_PATH_LOCKED());
        result.add(MILESTONE_7_LOCKED());
        return result;
    }
    public ItemStack REFRESH_BUTTON() {
        return ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE).name(ChatColor.GREEN + "Refresh Stats").lore(ChatColor.GRAY + "Click to refresh all values!").make();
    }
    public ItemStack REFRESHING_PLACEHOLDER() {
        return ItemCreator.of(CompMaterial.BARRIER).name(ChatColor.RED + "Refreshing stats...").lore(ChatColor.RED + "Please wait!").make();
    }
    public ItemStack MILESTONE_PATH_LOCKED() {
        return ItemCreator.of(CompMaterial.IRON_NUGGET).name(String.valueOf(ChatColor.RED) + ChatColor.ITALIC + "Achievement Path locked").make();
    }
    public ItemStack MILESTONE_1_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "10min").make();
    }
    public ItemStack MILESTONE_2_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "1h").make();
    }
    public ItemStack MILESTONE_3_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "10h").make();
    }
    public ItemStack MILESTONE_4_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "24h").make();
    }
    public ItemStack MILESTONE_5_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "48h").make();
    }
    public ItemStack MILESTONE_6_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "72h").make();
    }
    public ItemStack MILESTONE_7_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "100h").make();
    }
    public ItemStack MILESTONE_8_LOCKED() {
        return ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "?").lore(ChatColor.GRAY + "10000h").make();
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
