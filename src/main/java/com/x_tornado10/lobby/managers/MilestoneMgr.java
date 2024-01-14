package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MilestoneMgr {
    private final Lobby plugin;
    private final FileConfiguration milestoneFile;
    private final List<Milestone> milestones;
    private final Database db;
    public MilestoneMgr() throws NullPointerException {
        plugin = Lobby.getInstance();
        milestoneFile = plugin.getConfigMgr().getMilestones();
        milestones = new ArrayList<>();
        if (!constructMilestones()) throw new NullPointerException();
        db = plugin.getDatabase();
    }

    private boolean constructMilestones() {
        if (milestoneFile == null) return false;
        ConfigurationSection sec = milestoneFile.getConfigurationSection("Milestones");
        if (sec == null) return false;
        for (String key : sec.getKeys(false)) {
            ConfigurationSection milestone = sec.getConfigurationSection(key);
            if (milestone == null) return false;
            int id = Integer.parseInt(key);
            String title = milestone.getString("Title");
            String subtitle = milestone.getString("Subtitle");
            String color = milestone.getString("Color");
            double playtime = milestone.getDouble("Playtime");
            milestones.add(new Milestone(id, title, subtitle, color, playtime));
        }
        final boolean[] err = {false};
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (db != null) saveToDB();
                    else this.runTaskLater(plugin, 10);
                } catch (SQLException e) {
                    err[0] = true;
                }
            }
        }.runTaskLater(plugin, 10);

        return !err[0];
    }
    public void saveToDB() throws SQLException {
        db.setMileStones(milestones.toArray(Milestone[]::new));
    }
    @Nullable
    public Milestone getMilestone(@NotNull Double playtime) {
        int id = 0;
        for (Milestone m : milestones) {
            if (m.playtime() <= playtime / 1000)
                if (m.id() > id) id = m.id();
        }
        return getMilestone(id);
    }
    @Nullable
    public Milestone getMilestone(@NotNull Integer id) {
        for (Milestone m : milestones) {
            if (m.id() == id) return m;
        }
        return null;
    }
    @Nullable
    public List<Milestone> getUnlockedMilestones(@NotNull Double playtime) {
        Milestone last = getMilestone(playtime);
        if (last == null) return null;
        int i = last.id();
        List<Milestone> milestones = new ArrayList<>();
        for (; i > 0; i--) {
            Milestone m = getMilestone(i);
            if (m == null) return null;
            milestones.add(m);
        }
        if (milestones.size() != i) return null;
        return milestones;
    }
    public int MILESTONE_COUNT() {
        return milestones.size();
    }
}
