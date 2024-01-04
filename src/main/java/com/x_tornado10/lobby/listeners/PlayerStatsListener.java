package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerStatsListener implements Listener {

    private final Database database;
    private final Logger logger;
    private final HashMap<UUID, Date> last_update;
    private final Lobby plugin;
    public PlayerStatsListener() {
        plugin = Lobby.getInstance();
        database = plugin.getDatabase();
        logger = plugin.getLogger();
        last_update = new HashMap<>();
        updateLoop();
    }
    public PlayerStats getPlayerStatsFromDatabase(Player player) throws SQLException {

        PlayerStats playerStats = database.findPlayerStatsByUUID(player.getUniqueId().toString());

        if (playerStats == null) {
            playerStats = new PlayerStats(player.getUniqueId().toString(), 0, 0, 0, 0, 0, new Date(),0,0, 0,0);
            database.createPlayerStats(playerStats);
        }

        return playerStats;
    }
    public PlayerStats getPlayerStatsFromDatabase(UUID uuid) throws SQLException {

        PlayerStats playerStats = database.findPlayerStatsByUUID(uuid.toString());

        if (playerStats == null) {
            playerStats = new PlayerStats(uuid.toString(), 0, 0, 0, 0, 0, new Date(),0,0, 0,0);
            database.createPlayerStats(playerStats);
        }

        return playerStats;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        last_update.put(p.getUniqueId(), new Date());
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setLast_login(new Date());
            LocalDate date = (new Date(playerStats.getLast_login().getTime())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!date.plusDays(1).isEqual(LocalDate.now())) {
                if (!date.plusDays(1).isEqual(LocalDate.now().plusDays(1))) playerStats.setLogin_streak(1);
            } else {
                playerStats.setLogin_streak(playerStats.getLogin_streak()+1);
            }
            playerStats.setLogins(playerStats.getLogins()+1);
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats." + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    private void updateLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Date> entry : last_update.entrySet()) {
                    if (Bukkit.getPlayer(entry.getKey()) != null) {
                        try {
                            PlayerStats playerStats = getPlayerStatsFromDatabase(entry.getKey());
                            playerStats.setPlaytime(playerStats.getPlaytime() + System.currentTimeMillis() - entry.getValue().getTime());
                            last_update.put(entry.getKey(), new Date());
                            database.updatePlayerStats(playerStats);
                        } catch (SQLException e) {
                            logger.severe("Could not update player stats." + e.getErrorCode());
                            e.printStackTrace();
                        }
                    } else last_update.remove(entry.getKey());
                }
            }
        }.runTaskTimer(plugin, 20,100);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setPlaytime(playerStats.getPlaytime() + System.currentTimeMillis() - last_update.get(p.getUniqueId()).getTime());
            last_update.remove(p.getUniqueId());
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats." + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    public static String convertSeconds(int seconds) {
        int days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setChat_messages_send(playerStats.getChat_messages_send()+1);
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats." + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity deadEntity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (!(deadEntity instanceof Player p)) {
            if (killer != null) {
                updateDeathStats(killer, true, true);
            }
            return;
        }

        updateDeathStats(p, false, false);

        if (killer != null) {
            updateDeathStats(killer, false, true);
        }
    }
    private void updateDeathStats(Player player, boolean isMobKill, boolean isKiller) {
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(player);
            if (isMobKill) {
                playerStats.setMob_kills(playerStats.getMob_kills() + 1);
            } else {
                if (isKiller) {
                    playerStats.setPlayer_kills(playerStats.getPlayer_kills() + 1);
                } else {
                    playerStats.setDeaths(playerStats.getDeaths() + 1);
                }
            }
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats. Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(e.getPlayer());
            playerStats.setBlocks_broken(playerStats.getBlocks_broken()+1);
            e.getPlayer().sendMessage(convertSeconds((int) playerStats.getPlaytime() / 1000));
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats. Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onPlaceBreak(BlockPlaceEvent e) {
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(e.getPlayer());
            playerStats.setBlocks_placed(playerStats.getBlocks_placed()+1);
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats. Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
}
