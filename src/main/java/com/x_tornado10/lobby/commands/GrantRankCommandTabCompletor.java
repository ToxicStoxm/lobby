package com.x_tornado10.lobby.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrantRankCommandTabCompletor implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    result.add(p.getName());
                }
            }
            case 2 -> {
                result.add("csp");
                result.add("cs+");
            }
        }
        return result;
    }
}
