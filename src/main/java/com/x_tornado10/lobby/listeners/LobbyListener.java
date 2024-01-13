package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.loops.ActionBarDisplay;
import com.x_tornado10.lobby.utils.Invs.LobbyCompass;
import com.x_tornado10.lobby.utils.Invs.LobbyPlayerStats;
import com.x_tornado10.lobby.utils.Invs.LobbyProfile;
import com.x_tornado10.lobby.utils.Item;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.FlowerPot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.List;

public class LobbyListener implements Listener{
    private final boolean buildMode;
    private final Lobby plugin;
    private final double reachDistance;
    private final List<Location> door;
    private final TreeMap<Integer, List<Location>> doorLayers;
    private boolean doorOpening = false;
    private boolean doorOpen = false;
    private boolean doorClosing = false;
    private final HashMap<UUID, Long> cooldown;

    public LobbyListener(boolean buildMode, List<Location> door) {
        this.buildMode = buildMode;
        plugin = Lobby.getInstance();
        reachDistance = 4.0;
        this.door = door;
        doorLayers = new TreeMap<>();
        for (Location loc : this.door) {
            if (doorLayers.containsKey(loc.getBlockY())) {
                List<Location> temp = doorLayers.get(loc.getBlockY());
                temp.add(loc);
                doorLayers.put(loc.getBlockY(), temp);
            } else {
                List<Location> temp = new ArrayList<>();
                temp.add(loc);
                doorLayers.put(loc.getBlockY(), temp);
            }
        }
        cooldown = new HashMap<>();
    }
    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        Player p = (Player) e.getInitiator().getViewers().get(0);
        if (!buildMode && isNotBuilder(p)) e.setCancelled(true);
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!buildMode && isNotBuilder(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!buildMode && isNotBuilder(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!buildMode && isNotBuilder(p)) e.setCancelled(true);
        } else if (e.getDamager() instanceof Player p && e.getEntity() instanceof ItemFrame) {
            if (!buildMode && isNotBuilder(p)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!buildMode && isNotBuilder(p)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if (loc.getY() >= 200 && !buildMode && isNotBuilder(p)) {
            if (!(e.getFrom().getY() >= 200)) e.setCancelled(true);
            loc.setY(199);
            p.teleport(loc);
        }
        if (loc.getY() <= -50 && !buildMode && isNotBuilder(p)) JoinListener.tpSpawn(p);
        p.setFoodLevel(20);

        if (isInPredefinedAreaOpen(p.getLocation())) {

            if (plugin.checkGroup(p,"csp") || plugin.checkGroup(p,"cs+") || !isNotBuilder(p)) {
                if (doorOpening || doorOpen) return;
                if (doorClosing) doorClosing = false;
                doorOpening = true;
                int delay = 0;
                BlockData bd = Bukkit.createBlockData("minecraft:air");
                for (Map.Entry<Integer, List<Location>> entry : doorLayers.entrySet()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Location loc0 : entry.getValue()) {
                                Block b = p.getWorld().getBlockAt(loc0);
                                b.setBlockData(bd.clone());
                            }
                        }
                    }.runTaskLater(plugin, delay);
                    delay += 5;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        doorOpening = false;
                        doorOpen = true;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (tryCloseDoor(p.getWorld())) cancel();
                            }
                        }.runTaskTimer(plugin, 0, 20);
                    }
                }.runTaskLater(plugin, delay);
                return;
            }
            if (isInPredefinedArea(p.getLocation())) {
                if (cooldown.containsKey(p.getUniqueId())) {
                    if (System.currentTimeMillis() - cooldown.get(p.getUniqueId()) >= 500) {
                        preventPlayer(p, e.getFrom());
                    }
                } else {
                    preventPlayer(p, e.getFrom());
                }
            }
        }
    }
    private void preventPlayer(Player p, Location loc) {
        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
        p.setVelocity(new Vector(loc.getX() - 1000, loc.getY() + 2, 0).normalize());
        p.sendMessage(ChatColor.RED + "You are not allowed to enter this area!");
        BaseComponent[] component = new ComponentBuilder(ChatColor.AQUA + "Buy ranks ")
                .append(String.valueOf(ChatColor.AQUA) + ChatColor.UNDERLINE.asBungee() + "here" + ChatColor.RESET)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://crafti-servi.com//plugin-resources/craftiservi/Final_Chart.png"))
                .append(ChatColor.AQUA + "!")
                .create();
        p.spigot().sendMessage(component);
    }
    private boolean tryCloseDoor(World w) {
        if (!doorOpen || doorClosing) return false;
        doorClosing = true;
        doorOpen = false;
        int delay = 0;
        final boolean[] err = {false};
        BlockData bd = Bukkit.createBlockData("minecraft:iron_bars[south=true, north=true]");
        TreeMap<Integer, List<Location>> temp = new TreeMap<>(Comparator.reverseOrder());
        temp.putAll(doorLayers);
        for (Map.Entry<Integer, List<Location>> entry : temp.entrySet()) {
            if (doorOpening && !doorClosing) break;
            for (Location ignored : entry.getValue()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (plugin.checkGroup(p, "csp") || plugin.checkGroup(p, "cs+") || !isNotBuilder(p) ) {
                        if (isInPredefinedAreaOpen(p.getLocation())) return false;
                    }
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Location loc0 : entry.getValue()) {
                        if (doorOpening && !doorClosing) cancel();
                        for (Location ignored : entry.getValue()) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (plugin.checkGroup(p, "csp") || plugin.checkGroup(p, "cs+") || !isNotBuilder(p) ) {
                                    if (isInPredefinedAreaOpen(p.getLocation())) {
                                        cancel();
                                        err[0] = true;
                                        return;
                                    }
                                }
                            }
                        }
                        if (!isBiggestKey(loc0.getBlockY())) {
                            if (!w.getBlockAt(new Location(w, loc0.getBlockX(), loc0.getBlockY()+1, loc0.getBlockZ())).getType().equals(Material.IRON_BARS)) {
                                cancel();
                                err[0] = true;
                                return;
                            }
                        }
                        Block b = w.getBlockAt(loc0);
                        b.setBlockData(bd.clone());
                    }
                }
            }.runTaskLater(plugin, delay);
            delay += 5;
        }
        return !err[0];
    }
    private boolean isBiggestKey(Integer i) {
        int smallest = Integer.MIN_VALUE;

        for (Integer key : doorLayers.keySet()) {
            if (key > smallest) {
                smallest = key;
            }
        }
        return i == smallest;
    }

    private boolean isInPredefinedArea(Location location) {
        for (Location door : door) {
            if (distanceX(door, location) <= 2 && distanceY(door, location) <= 0.5 && distanceZ(door, location) <= 0.5) {
                return true;
            }
        }
        return false;
    }
    private boolean isInPredefinedAreaOpen(Location location) {
        for (Location door : door) {
            if (distanceX(door, location) <= 3.5 && distanceY(door, location) <= 0.5 && distanceZ(door, location) <= 0.5) {
                return true;
            }
        }
        return false;
    }

    private double distanceX(Location loc1, Location loc2) {
        double x1 = loc1.getX();
        double x2 = loc2.getX();
        return Math.max(x1, x2) - Math.min(x1, x2);
    }

    private double distanceY(Location loc1, Location loc2) {
        double y1 = loc1.getY();
        double y2 = loc2.getY();
        return Math.max(y1, y2) - Math.min(y1, y2);
    }

    private double distanceZ(Location loc1, Location loc2) {
        double z1 = loc1.getZ();
        double z2 = loc2.getZ();
        return Math.max(z1, z2) - Math.min(z1, z2);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!buildMode && isNotBuilder(e.getEntity())) {
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
        Player p = (Player) e.getWhoClicked();
        if (!buildMode && isNotBuilder(p)) {
            if (e.getClickedInventory() == null) return;
            ItemStack i = e.getClickedInventory().getItem(e.getSlot());
            if (i == null) {
                if (e.getClick().isKeyboardClick()) {
                    i = p.getInventory().getItem(e.getHotbarButton());
                }
            }
            if (i == null) return;
            ItemMeta meta = i.getItemMeta();
            if (meta == null) return;
            if (!meta.hasCustomModelData()) return;
            switch (meta.getCustomModelData()) {
                case Item.LOBBYCOMPASS, Item.LOBBYHEAD -> e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        if (isValidAction(action)) {
            if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                Block clickedBlock = e.getClickedBlock();
                if (clickedBlock != null && action == Action.RIGHT_CLICK_BLOCK) {
                    if (clickedBlock.getType().toString().contains("TRAPDOOR") || clickedBlock.getType().equals(Material.FLOWER_POT) || clickedBlock.getType().equals(Material.COMPOSTER) || clickedBlock.getType().name().startsWith("POTTED_")) {
                        if (!buildMode && isNotBuilder(p)) e.setCancelled(true);
                    }
                }
            }
            ItemStack i = e.getItem();
            if (i != null) {
                ItemMeta itemMeta = i.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.hasCustomModelData()) {
                        switch (itemMeta.getCustomModelData()) {
                            case Item.LOBBYCOMPASS -> {
                                new LobbyCompass().displayTo(p);
                                e.setCancelled(true);
                            }
                            case Item.LOBBYHEAD -> {
                                if (!p.getOpenInventory().getType().equals(InventoryType.CHEST)) {
                                    new LobbyProfile(p).displayTo(p);
                                    e.setCancelled(true);
                                }
                            }
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean bool = false;
        for (Entity entity : player.getNearbyEntities(reachDistance, reachDistance, reachDistance)) {
            if (entity instanceof Player otherPlayer && entity != player) {

                Vector betweenPlayers = otherPlayer.getLocation().toVector().subtract(player.getLocation().toVector());

                if (betweenPlayers.lengthSquared() <= reachDistance * reachDistance &&
                        player.getLocation().getDirection().dot(betweenPlayers.normalize()) > 0.95) {
                    ActionBarDisplay a = JoinListener.displays.get(player.getUniqueId());
                    if (a != null) {
                        a.prio_message = " (Right click to see " + otherPlayer.getName() + "'s stats)";
                        a.prio_color = net.md_5.bungee.api.ChatColor.of("#0495cf");
                        bool = true;
                    }
                }
            }
        }
        if (!bool) {
            ActionBarDisplay a = JoinListener.displays.get(player.getUniqueId());
            if (a != null) {
                a.prio_message = "";
                a.prio_color = null;
            }
        }
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
        if (e.getRightClicked() instanceof Player p) {
            new LobbyPlayerStats(p).displayTo(e.getPlayer());
        } else if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME) && !buildMode && isNotBuilder(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!buildMode && isNotBuilder(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (!buildMode && isNotBuilder(e.getPlayer())) e.setCancelled(true);
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
    private boolean isNotBuilder(Player p) {
        Lobby plugin = Lobby.getInstance();
        return !plugin.checkGroup(p, "builder");
    }
}
