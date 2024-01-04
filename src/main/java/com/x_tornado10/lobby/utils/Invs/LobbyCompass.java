package com.x_tornado10.lobby.utils.Invs;

import com.comphenix.net.bytebuddy.asm.Advice;
import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.Server;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonReturnBack;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class LobbyCompass extends Menu {
    private final Lobby plugin;

    @Position(22)
    private final Button joinLobbyButton;
    @Position(24)
    private final Button joinEternalSMPButton;
    @Position(20)
    private final Button joinSurvivalButton;

    public LobbyCompass() {
        plugin = Lobby.getInstance();
        setSize(9 * 5);
        setTitle(ChatColor.DARK_GRAY + "Game Menu");

        joinLobbyButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (!clickType.isRightClick() && !clickType.isLeftClick()) return;
                if (Lobby.isLobby) {
                    player.sendMessage(ChatColor.RED + "You are already connected to Lobby!");
                    Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                    menu.handleClose(currentChestInventory);
                    player.closeInventory();
                } else {
                   plugin.sendPlayerToServer(player, Server.Lobby);
                }
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.BOOKSHELF)
                        .name(ChatColor.GREEN + "Lobby")
                        .lore(
                                "",
                                ChatColor.GRAY + "Return to the Lobby"
                        )
                        .make();
            }
        };

        joinEternalSMPButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                plugin.sendPlayerToServer(player, Server.EternalSMP);
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                        .name(ChatColor.GREEN + "EternalSMP " + ChatColor.RED + "[1.20.1]")
                        .lore(
                                ChatColor.DARK_GRAY + "Survival",
                                "",
                                ChatColor.GRAY + "Join the EternalSMP server."
                        )
                        .make();
            }
        };

        joinSurvivalButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                plugin.sendPlayerToServer(player, Server.Survival);
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                        .name(ChatColor.GREEN + "Survival " + ChatColor.RED + "[1.20.2]")
                        .lore(
                                ChatColor.DARK_GRAY + "Survival",
                                "",
                                ChatColor.GRAY + "Join the Survival server"
                        )
                        .make();
            }
        };

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

}
