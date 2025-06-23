package com.x_tornado10.lobby.utils.Invs;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.Server;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class LobbyCompass extends Menu {
    private final Lobby plugin;

    @Position(20)
    private final Button joinLobbyButton;
    @Position(21)
    private final Button joinRevolutionSMPButton;

    @Position(22)
    private final Button joinSMP2025;

    @Position(23)
    private final Button joinSummerSMP2025;

    public LobbyCompass() {
        plugin = Lobby.getInstance();
        setSize(9 * 5);
        setTitle(ChatColor.DARK_GRAY + "Game Menu");

        joinLobbyButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                if (!clickType.isRightClick() && !clickType.isLeftClick()) return;
                player.sendMessage(ChatColor.RED + "You are already connected to Lobby!");
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
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

        joinRevolutionSMPButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                plugin.sendPlayerToServer(player, Server.RevolutionSMP);
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                        .name(ChatColor.GREEN + "RevolutionSMP " + ChatColor.RED + "[1.20.x]")
                        .lore(
                                ChatColor.DARK_GRAY + "Survival",
                                "",
                                ChatColor.GRAY + "Join the RevolutionSMP server."
                        )
                        .make();
            }
        };

        joinSMP2025 = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                plugin.sendPlayerToServer(player, Server.SMP2025);
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                        .name(ChatColor.GREEN + "SMP2025 " + ChatColor.RED + "[1.21.4]")
                        .lore(
                                ChatColor.DARK_GRAY + "Survival",
                                "",
                                ChatColor.GRAY + "Join the SMP2025 server."
                        )
                        .make();
            }
        };

        joinSummerSMP2025 = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                plugin.sendPlayerToServer(player, Server.SMP2025);
                Inventory currentChestInventory = player.getOpenInventory().getTopInventory();
                menu.handleClose(currentChestInventory);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GRASS_BLOCK)
                        .name(ChatColor.GREEN + "Summer-SMP2025 " + ChatColor.RED + "[1.21.5]" + ChatColor.AQUA + " [NEW]")
                        .lore(
                                ChatColor.DARK_GRAY + "Survival",
                                "",
                                ChatColor.GRAY + "Join the Summer-SMP2025 server."
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
