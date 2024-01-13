package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MilestoneMgr {
    private final List<Milestone> milestones;

    public MilestoneMgr() throws SQLException {
        Lobby plugin = Lobby.getInstance();
        milestones = new ArrayList<>();
        Database db = plugin.getDatabase();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    milestones.addAll(db.getMilestones());
                } catch (SQLException e) {
                    this.runTaskLater(plugin, 10);
                }
            }
        }.runTaskLater(plugin, 10);
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
    public List<Milestone> getUnlockedMilestones(Double playtime) {
        Milestone last = getMilestone(playtime);
        if (last == null) return null;
        int i = last.id();
        List<Milestone> milestones = new ArrayList<>();
        for (; i > 0; i--) {
            Milestone m = getMilestone(i);
            if (m == null) return null;
            milestones.add(m);
        }
        return milestones;
    }
}
