package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import com.x_tornado10.lobby.db.Database;
import com.x_tornado10.lobby.utils.custom.data.Milestone;
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
        milestones.addAll(db.getMilestones());
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
}
