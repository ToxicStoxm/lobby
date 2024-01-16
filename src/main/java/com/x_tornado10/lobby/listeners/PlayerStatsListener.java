package com.x_tornado10.lobby.listeners;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.managers.MilestoneMgr;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
import com.x_tornado10.lobby.utils.statics.Convertor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class PlayerStatsListener implements Listener {

    private final Database database;
    private final Logger logger;
    private final HashMap<UUID, Date> last_update;
    private final Lobby plugin;
    private final MilestoneMgr milestoneMgr;
    private LuckPerms lpAPI;
    public PlayerStatsListener() {
        plugin = Lobby.getInstance();
        database = plugin.getDatabase();
        logger = plugin.getLogger();
        last_update = new HashMap<>();
        milestoneMgr = plugin.getMilestonesMgr();
        lpAPI = plugin.getLpAPI();
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
            LocalDate date = (new Date(playerStats.getLast_login().getTime())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!date.plusDays(1).isEqual(LocalDate.now())) {
                if (!date.plusDays(1).isEqual(LocalDate.now().plusDays(1))) playerStats.setLogin_streak(1);
            } else {
                playerStats.setLogin_streak(playerStats.getLogin_streak()+1);
            }
            playerStats.setLast_login(new Date());
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
                            long d = playerStats.getPlaytime() + System.currentTimeMillis() - last_update.get(entry.getKey()).getTime();
                            playerStats.setPlaytime(d);
                            Player p = Bukkit.getPlayer(entry.getKey());
                            displayMilestone(p, checkMilestones(d,p));
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
            if (!last_update.containsKey(p.getUniqueId())) return;
            long d = playerStats.getPlaytime() + System.currentTimeMillis() - last_update.get(p.getUniqueId()).getTime();
            playerStats.setPlaytime(d);
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
    private Milestone checkMilestones(double playtime, Player p) {
        Milestone m = milestoneMgr.getMilestone(playtime);
        if (m == null) return null;
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
        UserManager mgr = lpAPI.getUserManager();
        GroupManager gmgr = lpAPI.getGroupManager();
        User usr = mgr.getUser(p.getUniqueId());
        if (usr == null) return null;
        for (PrefixNode node :  Objects.requireNonNull(gmgr.getGroup(usr.getPrimaryGroup())).getNodes(NodeType.PREFIX)) {
            if (Convertor.containsHexCode(node.getMetaValue())) {
                String currentHex = Convertor.extractHexCode(node.getMetaValue());
                if (currentHex != null) {
                    if (!currentHex.equals(m.color())) {
                        boolean granted = false;
                        for (PrefixNode node0 : usr.getNodes(NodeType.PREFIX)) {
                            if (Convertor.containsHexCode(node0.getMetaValue())) {
                                String currentHex0 = Convertor.extractHexCode(node0.getMetaValue());
                                if (currentHex0 != null && currentHex0.equals(m.color())) {
                                    granted = true;
                                    break;
                                }
                            }
                        }
                        if (!granted) {
                            for (Node n : usr.getNodes()) {
                                if (n.getType() == NodeType.PREFIX) {
                                    usr.data().remove(n);
                                }
                            }
                            usr.data().add(PrefixNode.builder(Convertor.replaceHexCodes(node.getMetaValue(), m.color()), 5).build());
                            mgr.saveUser(usr);
                            return m;
                        }
                    } else {
                        return null;
                    }
                }
            }

        }
        return null;
    }
    private boolean displayMilestone(Player p, @Nullable Milestone m) {
        if (m == null) return false;
        String title = m.title();
        String subtitle = m.subtitle();
        String color = m.color();
        String sub_color = Convertor.darkenHexColor(color, Convertor.DEFAULT);

        p.sendTitle(ChatColor.of(color) + "Unlocked milestone - " + title, ChatColor.of(sub_color) + subtitle, Convertor.TITLE_FADEIN, Convertor.TITLE_STAY, Convertor.TITLE_FADEOUT);
        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 999999999,1);
        return true;
    }
    public void updatePrefix(Player p) {
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
        UserManager mgr = lpAPI.getUserManager();
        GroupManager gmgr = lpAPI.getGroupManager();
        User usr = mgr.getUser(p.getUniqueId());
        if (usr == null) return;
        for (PrefixNode node0 : usr.getNodes(NodeType.PREFIX)) {
            for (PrefixNode node : Objects.requireNonNull(gmgr.getGroup(usr.getPrimaryGroup())).getNodes(NodeType.PREFIX)) {
                if (Convertor.containsHexCode(node0.getMetaValue())) {
                    String color = Convertor.extractHexCode(node0.getMetaValue());
                    for (Node n : usr.getNodes()) {
                        if (n.getType() == NodeType.PREFIX) {
                            usr.data().remove(n);
                        }
                    }
                    usr.data().add(PrefixNode.builder(Convertor.replaceHexCodes(node.getMetaValue(), color), 5).build());
                }
            }
        }
        mgr.saveUser(usr);
        lpAPI.runUpdateTask();
    }

}
