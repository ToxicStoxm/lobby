package com.x_tornado10.lobby.utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Item {
    public static List<UUID> ignore = null;
    public static List<Integer> BOUNDS53 = null;
    public static List<Integer> BOUNDS26 = null;
    public static List<Integer> BOUNDS44 = null;
    public static List<Integer> STATS = null;
    public static List<Integer> MILESTONE_BOUNDS = null;
    public static List<Integer> LOCKED_PLACEHOLDERS = null;
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
        MILESTONE_BOUNDS = new ArrayList<>();
        MILESTONE_BOUNDS.addAll(BOUNDS53);
        MILESTONE_BOUNDS.add(11);
        MILESTONE_BOUNDS.add(15);
        MILESTONE_BOUNDS.add(20);
        MILESTONE_BOUNDS.add(22);
        MILESTONE_BOUNDS.add(24);
        MILESTONE_BOUNDS.add(29);
        MILESTONE_BOUNDS.add(31);
        MILESTONE_BOUNDS.add(33);
        MILESTONE_BOUNDS.add(40);
        LOCKED_PLACEHOLDERS = new ArrayList<>();
        LOCKED_PLACEHOLDERS.addAll(findMissingNumbers(MILESTONE_BOUNDS));

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
    public static @NotNull List<Integer> findMissingNumbers(@NotNull List<Integer> inputList) {
        if (inputList.isEmpty()) {
            return Collections.emptyList();
        }
        int min = Collections.min(inputList);
        int max = Collections.max(inputList);
        List<Integer> missingNumbers = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            if (!inputList.contains(i)) {
                missingNumbers.add(i);
            }
        }
        Collections.sort(missingNumbers);
        return missingNumbers;
    }

}
