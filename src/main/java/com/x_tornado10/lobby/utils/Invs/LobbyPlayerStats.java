package com.x_tornado10.lobby.utils.Invs;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class LobbyPlayerStats extends Menu {
    private final Player p;
    public LobbyPlayerStats(Player p) {
        setSize(3 * 9);
        setTitle(ChatColor.DARK_GRAY + "Stats");
        this.p = p;
        List<ItemStack> items = Lobby.getInstance().getItemGetter().getStats(p);
        if (items != null) {
            List<Integer> slots = Item.STATS;
            int i = 0;
            for (ItemStack item : items) {
                setItem(slots.get(i), item);
                i++;
            }
        }
    }

    @Override
    protected void onPostDisplay(Player viewer) {
        for (Integer i : Item.BOUNDS26) {
            if (i != getReturnButtonPosition()) {
                setItem(i, Item.BOUNDS());
            } else if (getParent() == null) {
                setItem(i, Item.BOUNDS());
            }
        }
        setItem(10, ItemCreator.of(CompMaterial.PLAYER_HEAD)
                .name(ChatColor.GREEN + "Your Stats")
                .lore(ChatColor.GRAY + "Keep grinding!")
                .skullOwner(p.getName())
                .make());
        super.onPostDisplay(viewer);
    }
}
