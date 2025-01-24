package com.x_tornado10.lobby.utils.Invs;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class LobbyPlayerStats extends Menu {
    private final Player p;
    @Position(26)
    private final Button closeButton;
    @Position(25)
    private final Button refreshButton;
    private long cooldown = System.currentTimeMillis() - 1500;
    public LobbyPlayerStats(Player p) {
        setSize(3 * 9);
        setTitle(ChatColor.DARK_GRAY + "Stats");
        this.p = p;

        closeButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return Lobby.getInstance().getItemGetter().CLOSE_BUTTON();
            }
        };
        refreshButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (System.currentTimeMillis() - cooldown >= 1500) {
                    cooldown = System.currentTimeMillis();
                    refresh(player);
                } else {
                    player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 999999999, 1);
                    player.sendMessage(ChatColor.RED + "Please wait before refreshing again!");
                }
            }

            @Override
            public ItemStack getItem() {
                return Lobby.getInstance().getItemGetter().REFRESH_BUTTON();
            }
        };
    }

    @Override
    protected void onPostDisplay(Player viewer) {
        drawItems();
        super.onPostDisplay(viewer);
    }
    private void drawItems() {
        if  (getViewer() == null) return;
        for (Integer i : Item.BOUNDS26) {
            if (i != getReturnButtonPosition() && i != 25) {
                setItem(i, Item.BOUNDS());
            }
        }
        List<ItemStack> items = Lobby.getInstance().getItemGetter().getStats(p);
        if (items != null) {
            List<Integer> slots = Item.STATS;
            int i = 0;
            for (ItemStack item : items) {
                setItem(slots.get(i), item);
                i++;
            }
        }
        setItem(10,ItemCreator.of(CompMaterial.PLAYER_HEAD)
                .name(ChatColor.GREEN + p.getName() + "'s Stats")
                .skullOwner(p.getName())
                .make());
    }
    private void refresh(Player p) {
        if (!p.getOpenInventory().getTitle().contains("Stats")) return;
        setTitle(ChatColor.DARK_GRAY + "Refreshing stats...");
        ItemStack item = Lobby.getInstance().getItemGetter().REFRESHING_PLACEHOLDER();
        if (item != null) {
            List<Integer> slots = Item.STATS;
            for (int i : slots) {
                setItem(i, item);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!p.getOpenInventory().getTitle().contains("stats")) return;
                    setTitle(ChatColor.DARK_GRAY + "Stats");
                    drawItems();
                } catch (Exception ignored) {

                }
            }
        }.runTaskLater(Lobby.getInstance(), 20);
    }
}
