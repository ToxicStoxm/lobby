package com.x_tornado10.lobby.commands;

import com.x_tornado10.lobby.Lobby;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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
        TextComponent component =  Component.text("")
                .append(MineDown.parse("__&#ffffff-#1a77c4&Click here for rank info!__"))
                    .clickEvent(ClickEvent.openUrl("https://crafti-servi.com//plugin-resources/craftiservi/Final_Chart.png"))
                            .hoverEvent(HoverEvent.showText(Component.text("").append(MineDown.parse("&#ffffff-#1a77c4&/rankinfo"))));
        Lobby.getInstance().adventure().player(p).sendMessage(component);
        return true;
    }
}
