package com.x_tornado10.lobby.utils;

import io.r2dbc.spi.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

public class Item {
    public static List<UUID> ignore = null;
    public static List<Integer> BOUNDS53 = null;
    public static List<Integer> BOUNDS26 = null;
    public static List<Integer> BOUNDS44 = null;
    public static List<Integer> STATS = null;
    private static void addToBounds53(int from, int to) {
        for (int i = from; i<=to; i++) {
            BOUNDS53.add(i);
        }
    }
    private static void addToBounds44(int from, int to) {
        for (int i = from; i<=to; i++) {
            BOUNDS44.add(i);
        }
    }
    private static void addToBounds26(int from, int to) {
        for (int i = from; i<=to; i++) {
            BOUNDS26.add(i);
        }
    }
    private static void addToSTATS(int from, int to) {
        for (int i = from; i<=to; i++) {
            STATS.add(i);
        }
    }
    public static void initialize() {
        BOUNDS53 = new ArrayList<>();
        addToBounds53(0,9);
        addToBounds53(17,18);
        addToBounds53(26,27);
        addToBounds53(35,36);
        addToBounds53(44,53);
        BOUNDS26 = new ArrayList<>();
        addToBounds26(0,9);
        addToBounds26(17,26);
        BOUNDS26.add(11);
        ignore = new ArrayList<>();
        BOUNDS44 = new ArrayList<>();
        addToBounds44(0,9);
        addToBounds44(17,18);
        addToBounds44(26,27);
        addToBounds44(35,44);
        STATS = new ArrayList<>();
        addToSTATS(12,15);
    }
    public static ItemStack BOUNDS() {
        return ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE).name(" ").make();
    }

    public final static int LOBBYCOMPASS = 42;
    public final static int CRAFTISERVI = 43;
    public final static int LOBBY = 44;
    public final static int ETERNALSMP = 45;
    public final static int SURVIVAL = 46;
    public final static int LOBBYHEAD = 47;
    public final static int PROFILESTATS = 48;
    public final static int INVBOUNDS = 49;
    public final static int PROFILEMILESTONES = 50;
    public final static int BACKBUTTON0 = 51;

}
