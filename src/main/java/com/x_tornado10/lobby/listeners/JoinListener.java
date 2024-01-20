package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.loops.ActionBarDisplay;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.statics.Perms;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    public JoinListener(Location spawn, String join_msg) {
        JoinListener.spawn = spawn;
        this.join_msg = new TextComponent(join_msg);
        plugin = Lobby.getInstance();
        displays = new HashMap<>();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        lobby(e.getPlayer());
    }
    private void lobby(Player p) {
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
        if (plugin.checkGroup(p, "csp") || plugin.checkGroup(p, "cs+")) p.setAllowFlight(true);
        inv(p);
        if (plugin.checkGroup(p, "default")) {
            plugin.setPlayerGroup(p,"player");
            p.sendTitle("", "", 1, 1, 1);
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
