package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.loops.ActionBarDisplay;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.custom.data.PlayerData;
import com.x_tornado10.lobby.utils.statics.Convertor;
import com.x_tornado10.lobby.utils.statics.Perms;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.util.*;

public class JoinListener implements Listener {
    private static Location spawn;
    public static HashMap<UUID, ActionBarDisplay> displays;
    private final TextComponent join_msg;
    private final Lobby plugin;
    public final HashMap<UUID, PlayerData> playerData;
    public JoinListener(Location spawn, String join_msg) {
        JoinListener.spawn = spawn;
        this.join_msg = new TextComponent(join_msg);
        plugin = Lobby.getInstance();
        displays = new HashMap<>();
        playerData = new HashMap<>();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        lobby(e.getPlayer(), e);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage("");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.spigot().sendMessage(MineDown.parse( Convertor.formatMessage(plugin.getPrefix(p.getUniqueId())) + p.getName() + Convertor.formatMessage(plugin.getSuffix(p.getUniqueId())) + " &#ffffff-#1a77c4&left the lobby"));
        }
    }
    private void lobby(Player p, PlayerJoinEvent event) {
        Database db = plugin.getDatabase();
        try {
            if (db.findPlayerStatsByUUID(String.valueOf(p.getUniqueId())) == null) {
                db.createPlayerStats(new PlayerStats(
                        String.valueOf(p.getUniqueId()),
                        0,
                        0,
                        0,
                        0,
                        0,
                        new Date(),
                        0,
                        0,
                        0,
                        0
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Encountered an error while communicating with the database! Please restart the server!");
        }
        JoinListener.tpSpawn(p);
        p.setFoodLevel(20);
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setExp(0);
        try {
            p.setLevel(plugin.getMilestonesMgr().getMilestone((double) plugin.getDatabase().findPlayerStatsByUUID(String.valueOf(p.getUniqueId())).getPlaytime()).id());
        } catch (SQLException | NullPointerException e) {
            p.setLevel(0);
        }
        p.setGameMode(GameMode.ADVENTURE);
        if (plugin.hasPremium(p)) p.setAllowFlight(true);
        inv(p);
        if (plugin.checkGroup(p, "default")) {
            plugin.setPlayerGroup(p,"player");
            p.spigot().sendMessage(new MineDown("&#ffffff-#1a77c4&Hey " + p.getName() + ", welcome to the Crafti-Servi-Network!").toComponent());
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 999999999,1);
        }
        event.setJoinMessage("");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.spigot().sendMessage(MineDown.parse(Convertor.formatMessage(plugin.getPrefix(p.getUniqueId())) + p.getName() + Convertor.formatMessage(plugin.getSuffix(p.getUniqueId())) + " &#ffffff-#1a77c4&joined the lobby"));
        }

        UUID pid = p.getUniqueId();
        String prefix = plugin.getPrefix_Null(pid);
        String suffix = plugin.getSuffix_Null(pid);
        String name = p.getName();
        if (prefix != null && suffix != null) {
            if (!playerData.containsKey(pid)) {
                playerData.put(pid, new PlayerData(pid, prefix, name, suffix));
            } else {
                PlayerData playerData1 = playerData.get(pid);
                playerData1.setPrefix(prefix);
                playerData1.setPlayer_name(name);
                playerData1.setSuffix(suffix);
            }
        }

        displays.put(p.getUniqueId(), new ActionBarDisplay(p,join_msg));
    }

    private void inv(Player p) {
        Inventory inv = p.getInventory();
        if (!plugin.hasPermission(p, Perms.builder)) inv.clear();
        inv.setItem(0, getCompass());
        inv.setItem(1, getHead(p));
    }
    private ItemStack getCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = compass.getItemMeta();
        Objects.requireNonNull(itemMeta).setDisplayName(ChatColor.GREEN + "Server Menu " + ChatColor.GRAY + "(Right Click)");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to open server menu menu!");
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(Item.LOBBYCOMPASS);
        compass.setItemMeta(itemMeta);
        return compass;
    }
    private ItemStack getHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta head_meta = (SkullMeta) head.getItemMeta();
        Objects.requireNonNull(head_meta).setDisplayName(ChatColor.GREEN + "My Profile " + ChatColor.GRAY + "(Right Click)");
        Objects.requireNonNull(head_meta).setOwningPlayer(p);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to open your profile!");
        head_meta.setLore(lore);
        head_meta.setCustomModelData(Item.LOBBYHEAD);
        head.setItemMeta(head_meta);
        return head;
    }

    public static void tpSpawn(Player p) {
        p.teleport(spawn);
    }
}
