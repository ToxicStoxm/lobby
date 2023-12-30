package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.logging.Logger;

public class PlayerStatsListener implements Listener {

    private final Database database;
    private final Logger logger;
    public PlayerStatsListener() {
        Lobby plugin = Lobby.getInstance();
        database = plugin.getDatabase();
        logger = plugin.getLogger();
    }
    public PlayerStats getPlayerStatsFromDatabase(Player player) throws SQLException {

        PlayerStats playerStats = database.findPlayerStatsByUUID(player.getUniqueId().toString());

        if (playerStats == null) {
            playerStats = new PlayerStats(player.getUniqueId().toString(), 0, 0, 0, 0, new Date(), new Date(),0);
            database.createPlayerStats(playerStats);
        }

        return playerStats;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setLast_login(new Date());
            playerStats.setLogins(playerStats.getLogins()+1);
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            logger.severe("Could not update player stats after join." + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        try {
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            p.sendMessage("Last Login: " + dateFormat.format(new Date(playerStats.getLast_login().getTime())) + "\n" + "Logins: " + playerStats.getLogins());
        } catch (SQLException ex) {
            logger.severe("Could not fetch Player stats." + ex.getErrorCode());
            ex.printStackTrace();
        }
    }
}
