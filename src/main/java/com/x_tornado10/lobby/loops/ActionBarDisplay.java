package com.x_tornado10.lobby.loops;


import com.x_tornado10.lobby.Lobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionBarDisplay {
    private final Player p;
    private final Lobby plugin;
    private final TextComponent join_msg;
    public ActionBarDisplay(Player p, TextComponent join_msg) {
        this.p = p;
        plugin = Lobby.getInstance();
        this.join_msg = join_msg;
        welcomeMessage();
    }
    private void loop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p == null || !p.isOnline()) {
                    cancel();
                    return;
                }
                refreshActionBar();
            }
        }.runTaskTimer(plugin, 0,1);
    }
    private void refreshActionBar() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat0 = new SimpleDateFormat("HH:mm");
        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = dateFormat0.format(currentDate);
        send(formattedDate + " | " + formattedTime + " [CET]");
    }
    private void welcomeMessage() {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (p == null || !p.isOnline()) return;
                if (i >= 4) {
                    cancel();
                    loop();
                }
                send(join_msg.getText().replace("%PLAYER%", p.getName()));
                i++;
            }
        }.runTaskTimer(Lobby.getInstance(), 0,20);
    }
    private void send(String s) {
        TextComponent comp = new TextComponent(prio_message.isEmpty() ? s : prio_message);
        comp.setColor(prio_color == null ? ChatColor.GRAY : prio_color);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, comp);
    }
    public String prio_message = "";
    public ChatColor prio_color;
}
