package com.x_tornado10.lobby.utils.Invs;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.managers.MilestoneMgr;
import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.x_tornado10.lobby.utils.Item;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.sql.SQLException;
import java.util.List;

public class LobbyProfile extends Menu {
    @Position(21)
    private final Button openStatsbutton;
    @Position(23)
    private final ButtonMenu openMilestonesButton;
    @Position(44)
    private final Button closeButton;

    public LobbyProfile(Player p) {
        setTitle(ChatColor.DARK_GRAY + "My Profile");
        setSize(9 * 5);

        this.openStatsbutton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new LobbyProfileStats(p).displayTo(player);
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
        closeButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                p.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return Lobby.getInstance().getItemGetter().CLOSE_BUTTON();
            }
        };

    }

    @Override
    protected void onPostDisplay(Player viewer) {
        for (Integer i : Item.BOUNDS44) {
            if (i != getReturnButtonPosition()) {
                setItem(i, Item.BOUNDS());
            }
        }
        super.onPostDisplay(viewer);
    }

    public class LobbyProfileMilestones extends Menu {

        @Position(48)
        private final Button lastPage;
        @Position(50)
        private final Button nextPage;
        private static boolean pages = false;
        private static int pagesC = 1;
        private int currentPage = 1;
        private final Lobby plugin;
        private final MilestoneMgr milestoneMgr;

        public LobbyProfileMilestones() {
            super(LobbyProfile.this);
            plugin = Lobby.getInstance();
            milestoneMgr = plugin.getMilestonesMgr();
            int i = (int) Math.ceil((double) milestoneMgr.MILESTONE_COUNT() / 8);
            setSize(9 * 6);
            if (i > 1) {
                plugin.getLogger().severe("DEBUG1");
                pages = true;
                pagesC = i;
            }
            plugin.getLogger().severe(pages + "     " + pagesC + "            " + i);
            setTitle(ChatColor.DARK_GRAY + "Milestones (" + currentPage + "/" + pagesC + ")");

            lastPage = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (pages) {
                        if (currentPage > 1) {
                            currentPage -= 1;
                            drawItems(player);
                            setTitle(ChatColor.DARK_GRAY + "Milestones (" + currentPage + "/" + pagesC + ")");
                        }
                    }
                }

                @Override
                public ItemStack getItem() {
                    if (!pages) return Item.BOUNDS();
                    else return plugin.getItemGetter().PAGE_BACK();
                }
            };

            nextPage = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (pages) {
                        if (currentPage < pagesC) {
                            currentPage += 1;
                            drawItems(player);
                            setTitle(ChatColor.DARK_GRAY + "Milestones (" + currentPage + "/" + pagesC + ")");
                        }
                    }
                }

                @Override
                public ItemStack getItem() {
                    if (!pages) return Item.BOUNDS();
                    else return plugin.getItemGetter().PAGE_NEXT();
                }
            };
        }

        @Override
        protected void onPostDisplay(Player viewer) {
            drawItems(viewer);
        }

        private void drawItems(Player p) {
            if (pages) {
                if (currentPage == 1) {
                    for (Integer i : Item.MILESTONE_BOUNDS) {
                        if (i != getReturnButtonPosition() && i != 48 && i != 50 && i != 17) {
                            setItem(i, Item.BOUNDS());
                        } else if (getParent() == null) {
                            setItem(i, Item.BOUNDS());
                        }
                    }
                } else {
                    for (Integer i : Item.MILESTONE_BOUNDS) {
                        if (i != getReturnButtonPosition() && i != 48 && i != 50 && i != 17 && i != 9) {
                            setItem(i, Item.BOUNDS());
                        } else if (getParent() == null) {
                            setItem(i, Item.BOUNDS());
                        }
                    }
                }

            } else {
                for (Integer i : Item.MILESTONE_BOUNDS) {
                    if (i != getReturnButtonPosition() && i != 48 && i != 50) {
                        setItem(i, Item.BOUNDS());
                    } else if (getParent() == null) {
                        setItem(i, Item.BOUNDS());
                    }
                }
            }
            PlayerStats stats;
            try {
                stats = plugin.getDatabase().findPlayerStatsByUUID(String.valueOf(p.getUniqueId()));
            } catch (SQLException e) {
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "Something went wrong while loading milestones!");
                return;
            }
            List<Milestone> milestones = milestoneMgr.getUnlockedMilestones((double) stats.getPlaytime());
            if (milestones == null) {
                for (int i : Item.MILESTONE_POS) {
                    Milestone m = milestoneMgr.getMilestone(Item.MILESTONE_POS.indexOf(i) + 1);
                    if (m == null) {
                        setItem(i, Item.BOUNDS());
                        for (int placeholder : Item.getPath(Item.MILESTONE_POS.indexOf(i) + 1 > 8 ? adjustBelowEight(Item.MILESTONE_POS.indexOf(i) + 1) : Item.MILESTONE_POS.indexOf(i) + 1, currentPage, pages)) {
                            if (placeholder != -1) setItem(placeholder, Item.BOUNDS());
                        }
                    } else {
                        setItem(i, ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "? - " + formatSeconds((long) m.playtime())).make());
                        for (int placeholder : Item.getPath(Item.MILESTONE_POS.indexOf(i) + 1 > 8 ? adjustBelowEight(Item.MILESTONE_POS.indexOf(i) + 1) : Item.MILESTONE_POS.indexOf(i) + 1, currentPage, pages)) {
                            if (placeholder != -1) setItem(placeholder, plugin.getItemGetter().MILESTONE_PATH_LOCKED());
                        }
                    }
                }
            } else {
                for (int i : Item.MILESTONE_POS) {
                    int milestone = currentPage * 8 - 7 + Item.MILESTONE_POS.indexOf(i);
                    if (milestones.size() <= milestone) {
                        Milestone m = milestoneMgr.getMilestone(milestone);
                        if (m == null) {
                            setItem(i, Item.BOUNDS());
                            for (int placeholder : Item.getPath(milestone > 8 ? adjustBelowEight(milestone) : milestone, currentPage, pages)) {
                                if (placeholder != -1) setItem(placeholder, Item.BOUNDS());
                            }
                        } else {
                            setItem(i, ItemCreator.of(CompMaterial.BLACK_CONCRETE).name(ChatColor.GRAY + "? - " + formatSeconds((long) m.playtime())).make());
                            for (int placeholder : Item.getPath(milestone > 8 ? adjustBelowEight(milestone) : milestone, currentPage, pages)) {
                                if (placeholder != -1) setItem(placeholder, plugin.getItemGetter().MILESTONE_PATH_LOCKED());
                            }
                        }
                    } else {
                        Milestone m = milestones.get(milestone);
                        setItem(i, ItemCreator.of(CompMaterial.GOLD_BLOCK).name(ChatColor.of(m.color()) + m.title() + " - " + formatSeconds((long) m.playtime())).make());
                        for (int placeholder : Item.getPath(milestone > 8 ? adjustBelowEight(milestone) : milestone, currentPage, pages)) {
                            if (placeholder != -1) setItem(placeholder, plugin.getItemGetter().MILESTONE_PATH_UNLOCKED());
                        }
                    }
                }
            }
        }
        public static int adjustBelowEight(int number) {
            while (number >= 8) {
                number -= 8;
            }
            return number;
        }
        private String formatSeconds(long seconds) {
            int h = (int) (seconds / 3600);
            seconds %= 3600;
            int m = (int) (seconds / 60);

            StringBuilder formattedTime = new StringBuilder();

            if (h > 0) {
                formattedTime.append(h).append("h ");
            }

            if (m > 0 || formattedTime.isEmpty()) {
                formattedTime.append(m).append("m");
            }

            return formattedTime.toString().replaceAll(" $", "");
        }

    }

    public class LobbyProfileStats extends Menu {

        @Position(25)
        private final Button refreshButton;
        private final Player p;
        private long cooldown = System.currentTimeMillis() - 1500;

        public LobbyProfileStats(Player p) {
            super(LobbyProfile.this);
            setSize(9 * 3);
            setTitle(ChatColor.DARK_GRAY + "Stats");
            this.p = p;


            refreshButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    if (System.currentTimeMillis() - cooldown >= 1500) {
                        cooldown = System.currentTimeMillis();
                        refresh();
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
        }

        private void drawItems() {
            for (Integer i : Item.BOUNDS26) {
                if (i != getReturnButtonPosition() && i != 25) {
                        setItem(i, Item.BOUNDS());
                } else if (getParent() == null) {
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
            setItem(10, ItemCreator.of(CompMaterial.PLAYER_HEAD)
                    .name(ChatColor.GREEN + "Your Stats")
                    .skullOwner(p.getName())
                    .make());
        }

        private void refresh() {
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
                        setTitle(ChatColor.DARK_GRAY + "Stats");
                        drawItems();
                    } catch (Exception ignored) {

                    }
                }
            }.runTaskLater(Lobby.getInstance(), 20);
        }

    }
}
