package com.x_tornado10.lobby.managers;

import com.x_tornado10.lobby.Lobby;
import org.bukkit.configuration.file.FileConfiguration;

public class MilestoneMgr {
    private FileConfiguration milestones;
    private
    public MilestoneMgr() {
        milestones = Lobby.getInstance().getConfigMgr().getMilestones();
    }
}
