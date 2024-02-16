package com.x_tornado10.lobby.playerstats;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
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
