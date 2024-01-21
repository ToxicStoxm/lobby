package com.x_tornado10.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tchristofferson.configupdater.ConfigUpdater;
import com.x_tornado10.lobby.chat.filters.LogFilter;
import com.x_tornado10.lobby.commands.*;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.listeners.JoinListener;
import com.x_tornado10.lobby.listeners.LobbyListener;
import com.x_tornado10.lobby.managers.ConfigMgr;
import com.x_tornado10.lobby.managers.MilestoneMgr;
import com.x_tornado10.lobby.placeholderapi.PlaceHolderHook;
import com.x_tornado10.lobby.updateLeaderboard.UpdateLeaderboard;
import com.x_tornado10.lobby.utils.Invs.Items.ItemGetter;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.statics.Convertor;
import com.x_tornado10.lobby.utils.statics.Paths;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.ButtonReturnBack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMaterial;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;


public final class Lobby extends SimplePlugin {

    public static Lobby getInstance() {
        return (Lobby) SimplePlugin.getInstance();
    }

    @Getter
    private ConfigMgr configMgr;

    private JoinListener joinListener;
    @Getter
    private Database database;
    private Logger logger;
    @Getter
    private LuckPerms lpAPI;
    @Getter
    private ItemGetter itemGetter;
    @Getter
    private MilestoneMgr milestonesMgr;
    @Getter
    private LogFilter logFilter;

    @Override
    protected void onPluginLoad() {
        logFilter = new LogFilter();
        super.onPluginLoad();
    }



    @Override
    public void onPluginStart() {
        // Plugin startup logic
        logger = getLogger();
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, new ArrayList<>());

        } catch (IOException e) {
            logger.severe("Error while trying to update config.yml!");
            logger.severe("If this error persists after restarting the server please file a bug report!");
        }
        reloadConfig();
        Paths.initialize();
        Item.initialize();
        configMgr = new ConfigMgr();
        database = new Database(configMgr.getDbCredentials());

        try {
            database.initialize();
            logger.info("Successfully initialized database.");
        } catch (SQLException e) {
            logger.severe("Couldn't initialize database! Disabling plugin!");
            logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            milestonesMgr = new MilestoneMgr();
        } catch (NullPointerException e) {
            logger.severe(e.getMessage());
            logger.severe("Wasn't able to get milestones from 'milestones.yml' please check your syntax!");
        }

        Menu.setSound(null);
        ButtonReturnBack.setItemStack(ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE).name(ChatColor.RED + "Go Back").lore(ChatColor.GRAY + "To my Profile").make());

        itemGetter = new ItemGetter();
        joinListener = new JoinListener(configMgr.spawn(), configMgr.joinMsg());
        PluginCommand grantRank = Bukkit.getPluginCommand("setrank");
        if (grantRank != null) {
            grantRank.setExecutor(new GrantRankCommand());
            grantRank.setTabCompleter(new GrantRankCommandTabCompletor());
        }
        PluginCommand setSuffix = Bukkit.getPluginCommand("setsuffix");
        if (setSuffix != null) {
            setSuffix.setExecutor(new SuffixChangeCommand());
        }
        PluginCommand rankInfo = Bukkit.getPluginCommand("rankinfo");
        if (rankInfo != null) {
            rankInfo.setExecutor(new RankInfo());
        }
        lpAPI = LuckPermsProvider.get();
        Bukkit.getPluginManager().registerEvents(joinListener, this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(configMgr.getDoor()), this);
        PluginCommand lobby = Bukkit.getPluginCommand("lobby");
        if (lobby != null) {
            lobby.setExecutor(new LobbyCommand());
        }
        PlaceHolderHook.registerHook();
        new UpdateLeaderboard(100, 100,  new String[]{"ajl updatealloffline lobby_playtime","ajl updatealloffline lobby_logins","ajl updatealloffline lobby_login_streak"});
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        LogFilter.blockedStrings = getStrings();

        logFilter.registerFilter();
    }

    @NotNull
    private static List<String> getStrings() {
        List<String> blockedStrings = new ArrayList<>();
        blockedStrings.add("Not all placeholders support updating offline players");
        blockedStrings.add("[ajLeaderboards]");
        blockedStrings.add("[OfflineUpdater]");
        blockedStrings.add("You can check the progress by either checking the console, or running");
        blockedStrings.add("Finished updating all offline players");
        blockedStrings.add("Started update of");
        return blockedStrings;
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public boolean hasPermission(Player p, String permission) {
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
        UserManager usrMgr = lpAPI.getUserManager();
        User usr = usrMgr.getUser(p.getUniqueId());
        if (usr == null) return false;
        return usr.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
    public boolean checkGroup (Player p, String groupName) {
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
        User usr = lpAPI.getUserManager().getUser(p.getUniqueId());
        GroupManager gm = lpAPI.getGroupManager();
        if (usr == null) return true;
        if (!gm.isLoaded(groupName)) gm.loadGroup(groupName);
        Group group = gm.getGroup(groupName);
        return usr.getInheritedGroups(QueryOptions.defaultContextualOptions()).contains(group);
    }
    public void setPlayerGroup(Player p, String groupName) {
        UserManager userManager = lpAPI.getUserManager();
        User user = userManager.getUser(p.getUniqueId());
        if (user == null) return;
        GroupManager groupManager = lpAPI.getGroupManager();
        Group group = groupManager.getGroup(groupName);
        if (group == null) return;
        for (InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) {
            user.data().remove(node);
        }
        InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
        user.data().add(inheritanceNode);
        user.setPrimaryGroup(groupName);
        updatePrefix(p);
        userManager.saveUser(user);
        lpAPI.runUpdateTask();
    }
    public void setPlayerGroup(OfflinePlayer p, String groupName) {
        UserManager userManager = lpAPI.getUserManager();
        User user = userManager.getUser(p.getUniqueId());
        if (user == null) return;
        GroupManager groupManager = lpAPI.getGroupManager();
        Group group = groupManager.getGroup(groupName);
        if (group == null) return;
        for (InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) {
            user.data().remove(node);
        }
        InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
        user.data().add(inheritanceNode);
        user.setPrimaryGroup(groupName);
        updatePrefix(p);
        userManager.saveUser(user);
        lpAPI.runUpdateTask();
    }
    public String getPrefix(UUID playerUUID) {
        UserManager userManager = lpAPI.getUserManager();
        User user = userManager.getUser(playerUUID);
        if (user == null) return "";
        userManager.loadUser(user.getUniqueId());
        user = userManager.getUser(playerUUID);
        if (user == null) return "";
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        if (prefix != null && !prefix.isEmpty()) return prefix;
        else return "";
    }
    public String getSuffix(UUID playerUUID) {
        User user = lpAPI.getUserManager().getUser(playerUUID);
        if (user == null) return "";
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String suffix = metaData.getSuffix();
        if (suffix != null && !suffix.isEmpty()) return suffix;
        else return "";
    }
    public void updatePrefix(Player p) {
        UserManager mgr = lpAPI.getUserManager();
        GroupManager gmgr = lpAPI.getGroupManager();
        User usr = mgr.getUser(p.getUniqueId());
        if (!mgr.isLoaded(p.getUniqueId())) {
            mgr.loadUser(p.getUniqueId());
            usr = mgr.getUser(p.getUniqueId());
        }
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
    public void updatePrefix(OfflinePlayer p) {
        UserManager mgr = lpAPI.getUserManager();
        GroupManager gmgr = lpAPI.getGroupManager();
        User usr = mgr.getUser(p.getUniqueId());
        if (!mgr.isLoaded(p.getUniqueId())) {
            mgr.loadUser(p.getUniqueId());
            usr = mgr.getUser(p.getUniqueId());
        }
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
