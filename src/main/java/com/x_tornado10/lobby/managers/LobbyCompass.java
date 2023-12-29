package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.utils.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LobbyCompass {
    private static Inventory menu;

    public LobbyCompass() {
        menu = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Game Menu");
        menu.setItem(13, Lobby.isLobby ? getCraftiServi() : getLobby());
        menu.setItem(29, getEternalSMP());
        menu.setItem(33, getSurvival());
    }
    public void openCompass(Player p) {
        Inventory menu = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Game Menu");
        menu.setContents(LobbyCompass.menu.getContents());
        p.openInventory(menu);
    }
    private ItemStack getLobby() {
        ItemStack lobby = new ItemStack(Material.BOOKSHELF);
        ItemMeta lobby_meta = lobby.getItemMeta();
        if (lobby_meta != null) {
            lobby_meta.setDisplayName(ChatColor.GREEN + "Lobby");
            List<String> lobby_lore = new ArrayList<>();
            lobby_lore.add("");
            lobby_lore.add(ChatColor.GRAY + "Return to the Lobby");
            lobby_meta.setLore(lobby_lore);
            lobby_meta.setCustomModelData(Item.LOBBY);
        }
        lobby.setItemMeta(lobby_meta);
        return lobby;
    }
    private ItemStack getCraftiServi() {
        ItemStack craftiservi = new ItemStack(Material.BOOKSHELF);
        ItemMeta craftiservi_meta = craftiservi.getItemMeta();
        if (craftiservi_meta != null) {
            craftiservi_meta.setDisplayName(ChatColor.GREEN + "Crafti-Servi-Network");
            List<String> craftiservi_lore = new ArrayList<>();
            craftiservi_lore.add("");
            craftiservi_lore.add(ChatColor.GRAY + "Welcome to the Crafti-Servi-Network");
            craftiservi_meta.setLore(craftiservi_lore);
            craftiservi_meta.setCustomModelData(Item.CRAFTISERVI);
        }
        craftiservi.setItemMeta(craftiservi_meta);
        return craftiservi;
    }
    private ItemStack getEternalSMP() {
        ItemStack eternalSMP = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta eternalSMP_meta = eternalSMP.getItemMeta();
        if (eternalSMP_meta != null) {
            eternalSMP_meta.setDisplayName(ChatColor.GREEN + "EternalSMP " + ChatColor.RED + "[1.20.1]");
            List<String> eternalSMP_lore = new ArrayList<>();
            eternalSMP_lore.add(ChatColor.DARK_GRAY + "Survival");
            eternalSMP_lore.add("");
            eternalSMP_lore.add(ChatColor.GRAY + "Join the EternalSMP server.");
            eternalSMP_meta.setLore(eternalSMP_lore);
            eternalSMP_meta.setCustomModelData(Item.ETERNALSMP);
        }
        eternalSMP.setItemMeta(eternalSMP_meta);
        return eternalSMP;
    }
    private ItemStack getSurvival() {
        ItemStack survival = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta survival_meta = survival.getItemMeta();
        if (survival_meta != null) {
            survival_meta.setDisplayName(ChatColor.GREEN + "Survival " + ChatColor.RED + "[1.20.2]");
            List<String> survival_lore = new ArrayList<>();
            survival_lore.add(ChatColor.DARK_GRAY + "Survival");
            survival_lore.add("");
            survival_lore.add(ChatColor.GRAY + "Join the Survival server");
            survival_meta.setLore(survival_lore);
            survival_meta.setCustomModelData(Item.SURVIVAL);
        }
        survival.setItemMeta(survival_meta);
        return survival;
    }
}
