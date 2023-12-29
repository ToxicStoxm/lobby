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
    public JoinListener(Location spawn, String join_msg) {
        JoinListener.spawn = spawn;
        this.join_msg = new TextComponent(join_msg);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        lobby(e.getPlayer());
    }
    private void lobby(Player p) {
        p.teleport(spawn);
        p.setFoodLevel(20);
        p.setTotalExperience(0);
        p.setLevel(0);

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
        new ActionBarDisplay(p,join_msg);
    }
    public static void tpSpawn(Player p) {
        p.teleport(spawn);
    }
}
