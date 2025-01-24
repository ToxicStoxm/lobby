package com.x_tornado10.lobby.utils.custom.data;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String prefix;
    private String player_name;
    private String suffix;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public PlayerData(UUID uuid, String prefix, String player_name, String suffix) {
        this.uuid = uuid;
        this.prefix = prefix;
        this.player_name = player_name;
        this.suffix = suffix;
    }

}
