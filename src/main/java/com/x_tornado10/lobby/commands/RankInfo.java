package com.x_tornado10.lobby.commands;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankInfo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("You need to be a player to execute this command!");
            return true;
        }

        p.playSound(p, Sound.ENTITY_VILLAGER_AMBIENT, 999999999, 1);
        BaseComponent[] component = new ComponentBuilder( "__&#ffffff-#1a77c4&Click here for rank info!__")
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://crafti-servi.com//plugin-resources/craftiservi/Final_Chart.png"))
                .create();
        p.spigot().sendMessage(MineDown.parse(MineDown.stringify(component)));
        return true;
    }
}
