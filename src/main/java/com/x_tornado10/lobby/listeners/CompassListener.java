package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.Server;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassListener implements Listener {
    private final Lobby plugin;
    public CompassListener() {
        plugin = Lobby.getInstance();
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equals(ChatColor.DARK_GRAY + "Game Menu")) return;
        ItemStack i = e.getClickedInventory().getItem(e.getSlot());
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        if (!meta.hasCustomModelData()) return;
        switch (meta.getCustomModelData()) {
            case Item.LOBBY -> {
                if (Lobby.isLobby) {
                    p.closeInventory();
                    p.sendMessage(ChatColor.RED + "You are already connected to Lobby!");
                } else {
                    plugin.sendPlayerToServer(p, Server.Lobby);
                }
            }
            case Item.CRAFTISERVI -> p.sendMessage(ChatColor.GREEN + "Thank you for playing on Crafti-Servi-Network");
            case Item.ETERNALSMP -> plugin.sendPlayerToServer(p, Server.EternalSMP);
            case Item.SURVIVAL -> plugin.sendPlayerToServer(p, Server.Survival);

        }
        e.setCancelled(true);
    }
}
