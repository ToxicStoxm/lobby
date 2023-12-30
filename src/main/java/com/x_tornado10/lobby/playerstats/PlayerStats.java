package com.x_tornado10.lobby.playerstats;

import java.util.Date;

public class PlayerStats {
    private long blocks_broken;
    private long blocks_placed;
    private int deaths;
    private int kills;
    private Date last_login;
    private Date last_logout;
    private int logins;
    private String uuid;

    public PlayerStats(String uuid, int deaths, int kills, long blocks_broken, long blocks_placed, Date last_login, Date last_logout, int logins) {
        this.blocks_broken = blocks_broken;
        this.blocks_placed = blocks_placed;
        this.deaths = deaths;
        this.kills = kills;
        this.last_login = last_login;
        this.last_logout = last_logout;
        this.logins = logins;
        this.uuid = uuid;
    }

    public long getBlocks_broken() {
        return blocks_broken;
    }

    public void setBlocks_broken(long blocks_broken) {
        this.blocks_broken = blocks_broken;
    }

    public long getBlocks_placed() {
        return blocks_placed;
    }

    public void setBlocks_placed(long blocks_placed) {
        this.blocks_placed = blocks_placed;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public Date getLast_logout() {
        return last_logout;
    }

    public void setLast_logout(Date last_logout) {
        this.last_logout = last_logout;
    }

    public int getLogins() {
        return logins;
    }

    public void setLogins(int logins) {
        this.logins = logins;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
