package com.x_tornado10.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tchristofferson.configupdater.ConfigUpdater;
import com.x_tornado10.lobby.commands.GrantRankCommand;
import com.x_tornado10.lobby.commands.GrantRankCommandTabCompletor;
import com.x_tornado10.lobby.commands.LobbyCommand;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.listeners.JoinListener;
import com.x_tornado10.lobby.listeners.LobbyListener;
import com.x_tornado10.lobby.listeners.PlayerStatsListener;
import com.x_tornado10.lobby.managers.ConfigMgr;
import com.x_tornado10.lobby.managers.MilestoneMgr;
import com.x_tornado10.lobby.utils.Invs.Items.ItemGetter;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.statics.Paths;
import lombok.Getter;
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
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.ButtonReturnBack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMaterial;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private PlayerStatsListener playerStatsListener;

    @Override
    protected void onPluginLoad() {
        ButtonReturnBack.setMaterial(CompMaterial.RED_STAINED_GLASS_PANE);
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
        lpAPI = LuckPermsProvider.get();
        Bukkit.getPluginManager().registerEvents(joinListener, this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(configMgr.getDoor()), this);
        PluginCommand lobby = Bukkit.getPluginCommand("lobby");
        if (lobby != null) {
            lobby.setExecutor(new LobbyCommand());
        }
        playerStatsListener = new PlayerStatsListener();
        Bukkit.getPluginManager().registerEvents(playerStatsListener, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
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
        playerStatsListener.updatePrefix(p);
        userManager.saveUser(user);
        lpAPI.runUpdateTask();
    }
}
