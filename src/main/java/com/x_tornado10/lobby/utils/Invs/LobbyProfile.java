package com.x_tornado10.lobby.utils.Invs;

import com.x_tornado10.lobby.utils.Item;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.ButtonReturnBack;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class LobbyProfile extends Menu {
    @Position(21)
    private final Button openStatsbutton;
    @Position(23)
    private final ButtonMenu openMilestonesButton;

    public LobbyProfile() {
        setTitle(ChatColor.DARK_GRAY + "Server Profile");
        setSize(9 * 5);

        this.openStatsbutton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new LobbyProfileStats().displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator
                        .of(CompMaterial.PLAYER_HEAD)
                        .skullOwner(getViewer().getDisplayName())
                        .name(ChatColor.GREEN + "Your Stats")
                        .lore(ChatColor.GRAY + "Click to see your stats!")
                        .make();
            }
        };
        this.openMilestonesButton = new ButtonMenu(new LobbyProfileMilestones(),
                CompMaterial.ANVIL,
                ChatColor.GREEN + "Milestones",
                ChatColor.GRAY + "Click to see your progress!"
                );

    }

    @Override
    protected void onPostDisplay(Player viewer) {
        for (Integer i : Item.BOUNDS44) {
            if (i != getReturnButtonPosition()) {
                setItem(i, Item.BOUNDS());
            } else if (getParent() == null) {
                setItem(i, Item.BOUNDS());
            }
        }
        super.onPostDisplay(viewer);
    }
    public class LobbyProfileMilestones extends Menu {

        public LobbyProfileMilestones() {
            super(LobbyProfile.this);
            setSize(9 * 6);
            setTitle(ChatColor.DARK_GRAY + "Milestones");
        }

        @Override
        protected void onPostDisplay(Player viewer) {
            for (Integer i : Item.BOUNDS53) {
                if (i != getReturnButtonPosition()) {
                    setItem(i, Item.BOUNDS());
                } else if (getParent() == null) {
                    setItem(i, Item.BOUNDS());
                }
            }
            super.onPostDisplay(viewer);
        }
    }
    public class LobbyProfileStats extends Menu {

        public LobbyProfileStats() {
            super(LobbyProfile.this);
            setSize(9 * 3);
            setTitle(ChatColor.DARK_GRAY + "Stats");
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
            setItem(10,ItemCreator.of(CompMaterial.PLAYER_HEAD)
                    .name(ChatColor.GREEN + "Your Stats")
                    .lore(ChatColor.GRAY + "Keep grinding!")
                    .skullOwner(viewer.getName())
                    .make());
            super.onPostDisplay(viewer);
        }
    }
}
