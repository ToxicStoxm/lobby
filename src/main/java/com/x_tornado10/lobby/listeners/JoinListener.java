package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.loops.ActionBarDisplay;
import com.x_tornado10.lobby.utils.Item;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class JoinListener implements Listener {
    private static Location spawn;
    private final TextComponent join_msg;
    private final HashMap<UUID, Integer> join_counter;
    private final Lobby plugin;
    public JoinListener(Location spawn, String join_msg) {
        plugin = Lobby.getInstance();
        JoinListener.spawn = spawn;
        this.join_msg = new TextComponent(join_msg);
        join_counter = plugin.getConfigMgr().getJoinCounter();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.teleport(spawn);
        lobby(p);
        p.setFoodLevel(20);
        p.setTotalExperience(0);
        p.setLevel(0);
        new ActionBarDisplay(p,join_msg);
        /*
        if (join_counter.containsKey(p.getUniqueId())) {
            join_counter.put(p.getUniqueId(), join_counter.get(p.getUniqueId()) + 1);
            p.setLevel(join_counter.get(p.getUniqueId()));
        } else {
            join_counter.put(p.getUniqueId(), 0);
        }

         */
    }
    private void lobby(Player p) {
        Inventory inv = p.getInventory();
        if (!Lobby.getInstance().getConfigMgr().isBuildMode()) inv.clear();
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = compass.getItemMeta();
        Objects.requireNonNull(itemMeta).setDisplayName(ChatColor.GREEN + "Server Menu " + ChatColor.GRAY + "(Right Click)");
        List<String> lore = new ArrayList<>(2);
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to open server menu menu!");
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(Item.COMPASS);
        compass.setItemMeta(itemMeta);
        inv.setItem(0, compass);
    }
    public static void tpSpawn(Player p) {
        p.teleport(spawn);
    }
    public void saveJoinCounter() {
        plugin.getConfigMgr().saveJoinCounter(join_counter);
    }
}
