package com.x_tornado10.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tchristofferson.configupdater.ConfigUpdater;
import com.x_tornado10.lobby.commands.GrantRankCommand;
import com.x_tornado10.lobby.commands.GrantRankCommandTabCompletor;
import com.x_tornado10.lobby.commands.LobbyCommand;
import com.x_tornado10.lobby.commands.SuffixChangeCommand;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.listeners.PlayerStatsListener;
import com.x_tornado10.lobby.managers.ConfigMgr;
import com.x_tornado10.lobby.managers.MilestoneMgr;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.statics.Paths;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.menu.button.ButtonReturnBack;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMaterial;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public final class Lobby extends SimplePlugin {

    public static Lobby getInstance() {
        return (Lobby) SimplePlugin.getInstance();
    }

    private ConfigMgr configMgr;
    private Database database;
    private Logger logger;
    private LuckPerms lpAPI;
    private MilestoneMgr milestonesMgr;
    private PlayerStatsListener playerStatsListener;

    public ConfigMgr getConfigMgr() {
        return configMgr;
    }

    public Database getDatabase() {
        return database;
    }

    public LuckPerms getLpAPI() {
        return lpAPI;
    }

    public MilestoneMgr getMilestonesMgr() {
        return milestonesMgr;
    }

    @Override
    protected void onPluginLoad() {
        logger = getLogger();
        ButtonReturnBack.setMaterial(CompMaterial.RED_STAINED_GLASS_PANE);
        super.onPluginLoad();
    }

    @Override
    public void onPluginStart() {
        // Plugin startup logic
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
            milestonesMgr = new MilestoneMgr();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            logger.severe("Wasn't able to get milestones from database please check your syntax!");
        }

        PluginCommand grantRank = Bukkit.getPluginCommand("setrank");
        if (grantRank != null) {
            grantRank.setExecutor(new GrantRankCommand());
            grantRank.setTabCompleter(new GrantRankCommandTabCompletor());
        }
        PluginCommand lobby = Bukkit.getPluginCommand("lobby");
        if (lobby != null) {
            lobby.setExecutor(new LobbyCommand());
        }
        PluginCommand setSuffix = Bukkit.getPluginCommand("setsuffix");
        if (setSuffix != null) {
            setSuffix.setExecutor(new SuffixChangeCommand());
        }
        playerStatsListener = new PlayerStatsListener();
        Bukkit.getPluginManager().registerEvents(playerStatsListener, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
        database.save();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void sendPlayerToServer(@NotNull Player player, String serverName) {
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
    public boolean checkGroups (Player p, String[] groupNames) {
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
        User usr = lpAPI.getUserManager().getUser(p.getUniqueId());
        GroupManager gm = lpAPI.getGroupManager();
        if (usr == null) return true;
        @org.checkerframework.checker.nullness.qual.NonNull Collection<Group> g = usr.getInheritedGroups(QueryOptions.defaultContextualOptions());
        for (String groupName : groupNames) {
            if (!gm.isLoaded(groupName)) gm.loadGroup(groupName);
            Group group = gm.getGroup(groupName);
            if (g.contains(group)) return true;
        }
        return false;
    }
    public boolean hasPremium(Player p) {
        return checkGroups(p, new String[]{"csp","cs+"});
    }
    public boolean hasModeration(Player p) {
        return checkGroups(p, new String[]{"owner","builder"});
    }
    public void setPlayerGroup(@NotNull Player p, String groupName) {
        if (lpAPI == null) lpAPI = LuckPermsProvider.get();
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
        playerStatsListener.updatePrefix(p);
        userManager.saveUser(user);
        lpAPI.runUpdateTask();
    }
}
