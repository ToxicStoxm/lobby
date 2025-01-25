package com.x_tornado10.lobby.playerstats;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

public class PlayerStats implements Cloneable {
    private String uuid;
    private long deaths;
    private long player_kills;
    private long mob_kills;
    private long blocks_broken;
    private long blocks_placed;
    private Date last_login;
    private long login_streak;
    private long logins;
    private long chat_messages_send;
    private long playtime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getDeaths() {
        return deaths;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public long getPlayer_kills() {
        return player_kills;
    }

    public void setPlayer_kills(long player_kills) {
        this.player_kills = player_kills;
    }

    public long getMob_kills() {
        return mob_kills;
    }

    public void setMob_kills(long mob_kills) {
        this.mob_kills = mob_kills;
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

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public long getLogin_streak() {
        return login_streak;
    }

    public void setLogin_streak(long login_streak) {
        this.login_streak = login_streak;
    }

    public long getLogins() {
        return logins;
    }

    public void setLogins(long logins) {
        this.logins = logins;
    }

    public long getChat_messages_send() {
        return chat_messages_send;
    }

    public void setChat_messages_send(long chat_messages_send) {
        this.chat_messages_send = chat_messages_send;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public PlayerStats(String uuid, long deaths, long player_kills, long mob_kills, long blocks_broken, long blocks_placed, Date last_login, long login_streak, long logins, long chat_messages_send, long playtime) {
        this.uuid = uuid;
        this.deaths = deaths;
        this.player_kills = player_kills;
        this.mob_kills = mob_kills;
        this.blocks_broken = blocks_broken;
        this.blocks_placed = blocks_placed;
        this.last_login = last_login;
        this.login_streak = login_streak;
        this.logins = logins;
        this.chat_messages_send = chat_messages_send;
        this.playtime = playtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStats that = (PlayerStats) o;
        return getPlaytime() == that.getPlaytime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlaytime());
    }

    @Override
    public PlayerStats clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (PlayerStats) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
