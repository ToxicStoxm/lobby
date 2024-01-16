package com.x_tornado10.lobby.commands;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.statics.Convertor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuffixChangeCommand implements CommandExecutor {
    private final Lobby plugin;
    private LuckPerms lpAPI;
    public SuffixChangeCommand() {
        plugin = Lobby.getInstance();
        lpAPI = plugin.getLpAPI();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player p) {
            if (plugin.checkGroup(p, "cs+") || plugin.checkGroup(p, "csp")) {
                switch (args.length) {
                    case 0 -> p.sendMessage(ChatColor.RED + "Please provide a new suffix!");
                    case 1 -> {
                        if (lpAPI == null) lpAPI = plugin.getLpAPI();
                        if (lpAPI == null) return false;
                        UserManager usrMgr = lpAPI.getUserManager();
                        User usr = usrMgr.getUser(p.getName());
                        if (usr == null) return false;
                        String suffix = ChatColor.RESET + args[0].replace("%_%", " ") + ChatColor.RESET;
                        if (rawSuffix(suffix).length() > 20) {
                            p.sendMessage(ChatColor.RED + "Your suffix can't be longer than 15 characters!");
                            return true;
                        }
                        for (SuffixNode snode : usr.getNodes(NodeType.SUFFIX)) {
                            usr.data().remove(snode);
                        }
                        usr.data().add(SuffixNode.builder(suffix, 5).build());
                        usrMgr.saveUser(usr);
                        lpAPI.runUpdateTask();
                    }
                    default -> p.sendMessage(ChatColor.RED + "You provided too many arguments!");
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            }
        } else {
            switch (args.length) {
                case 0,1 -> plugin.getLogger().info("Please provide all necessary arguments!");
                case 2 -> {
                    if (lpAPI == null) lpAPI = plugin.getLpAPI();
                    if (lpAPI == null) return false;
                    UserManager usrMgr = lpAPI.getUserManager();
                    User usr = usrMgr.getUser(args[0]);
                    if (usr == null) return false;
                    String suffix = ChatColor.RESET + args[1].replace("%_%", " ") + ChatColor.RESET;
                    if (rawSuffix(suffix).length() > 20) {
                        plugin.getLogger().info(ChatColor.RED + "Your suffix can't be longer than 15 characters!");
                        return true;
                    }
                    for (SuffixNode snode : usr.getNodes(NodeType.SUFFIX)) {
                        usr.data().remove(snode);
                    }
                    usr.data().add(SuffixNode.builder(suffix, 5).build());
                    usrMgr.saveUser(usr);
                    lpAPI.runUpdateTask();
                }
                default -> plugin.getLogger().info(ChatColor.RED + "You provided too many arguments!");
            }
        }
        return true;
    }
    private String rawSuffix(String suffix) {
        return net.md_5.bungee.api.ChatColor.stripColor(suffix);
    }
}
