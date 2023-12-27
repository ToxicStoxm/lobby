package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyListener implements Listener {
    private final boolean buildMode;
    public LobbyListener(boolean buildMode) {
        this.buildMode = buildMode;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if (loc.getY()>=200 && !buildMode) e.setCancelled(true);
        if (loc.getY()<=-50 && !buildMode) JoinListener.tpSpawn(p);
        p.setFoodLevel(20);
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!buildMode) {
            Player p = e.getEntity();
            e.setDeathMessage("");
            e.setKeepLevel(true);
            e.setKeepInventory(true);
            e.setDroppedExp(0);
            e.getDrops().clear();
            p.spigot().respawn();
        }
    }
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onInventoryItemMove(InventoryMoveItemEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        if (isValidAction(action)) {
            ItemStack i = e.getItem();
            if (i != null) {
                ItemMeta itemMeta = i.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.hasCustomModelData()) {
                        if (itemMeta.getCustomModelData() == Item.COMPASS) {
                            Lobby.lobbyCompass.openCompass(p);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private boolean isValidAction(Action action) {
        return action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity().getType() != EntityType.ITEM_FRAME) e.setCancelled(true);
    }
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
        e.getWorld().setClearWeatherDuration(999999999);
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (!buildMode) e.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (!buildMode) e.setCancelled(true);
    }
    @EventHandler
    public void onExplosion(BlockExplodeEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (e.getBlock().getType().equals(Material.SNOW) || e.getBlock().getType().equals(Material.SNOW_BLOCK)) {
            e.setCancelled(true);
        }
    }
}
