package com.x_tornado10.lobby.db;

import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mineacademy.fo.database.SimpleDatabase;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Database extends SimpleDatabase {

    public Database(List<String> credentials) {
        String[] parts = credentials.get(0).split(":");
        String host = parts[2].strip().replace("/","");
        String[] parts1 = parts[3].strip().split("/");
        setConnectUsingHikari(true);
        connect(host, Integer.parseInt(parts1[0]),parts1[1],credentials.get(1),credentials.get(2),"player_stats",true);
    }

    public void initialize() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "deaths BIGINT, " +
                "player_kills BIGINT, " +
                "mob_kills BIGINT, " +
                "blocks_broken BIGINT, " +
                "blocks_placed BIGINT, " +
                "last_login DATETIME, " +
                "login_streak BIGINT, " +
                "logins BIGINT," +
                "chat_messages_send BIGINT," +
                "playtime BIGINT" +
                ")";
        PreparedStatement statement = prepareStatement(sql);
        statement.execute(sql);
        statement.close();
    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {
        PreparedStatement statement = prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        PlayerStats playerStats;

        if(resultSet.next()){

            playerStats = new PlayerStats(resultSet.getString("uuid"),
                    resultSet.getLong("deaths"),
                    resultSet.getLong("player_kills"),
                    resultSet.getLong("mob_kills"),
                    resultSet.getLong("blocks_broken"),
                    resultSet.getLong("blocks_placed"),
                    resultSet.getDate("last_login"),
                    resultSet.getLong("login_streak"),
                    resultSet.getLong("logins"),
                    resultSet.getLong("chat_messages_send"),
                    resultSet.getLong("playtime"));

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }

    public void createPlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = prepareStatement("INSERT INTO player_stats(uuid, deaths, player_kills, mob_kills, blocks_broken, blocks_placed, last_login, login_streak, logins, chat_messages_send, playtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, playerStats.getUuid());
        statement.setLong(2, playerStats.getDeaths());
        statement.setLong(3, playerStats.getPlayer_kills());
        statement.setLong(4, playerStats.getMob_kills());
        statement.setLong(5, playerStats.getBlocks_broken());
        statement.setLong(6, playerStats.getBlocks_placed());
        statement.setDate(7, new Date(playerStats.getLast_login().getTime()));
        statement.setLong(8, playerStats.getLogin_streak());
        statement.setLong(9, playerStats.getLogins());
        statement.setLong(10, playerStats.getChat_messages_send());
        statement.setLong(11, playerStats.getPlaytime());

        statement.executeUpdate();

        statement.close();

    }

    public void updatePlayerStats(PlayerStats playerStats) throws SQLException {
        PreparedStatement statement = prepareStatement("UPDATE player_stats SET deaths = ?, player_kills = ?, mob_kills = ?, blocks_broken = ?, blocks_placed = ?, last_login = ?, login_streak = ?, logins = ?, chat_messages_send = ?, playtime = ? WHERE uuid = ?");
        statement.setLong(1, playerStats.getDeaths());
        statement.setLong(2, playerStats.getPlayer_kills());
        statement.setLong(3, playerStats.getMob_kills());
        statement.setLong(4, playerStats.getBlocks_broken());
        statement.setLong(5, playerStats.getBlocks_placed());
        statement.setDate(6, new Date(playerStats.getLast_login().getTime()));
        statement.setLong(7, playerStats.getLogin_streak());
        statement.setLong(8, playerStats.getLogins());
        statement.setLong(9, playerStats.getChat_messages_send());
        statement.setLong(10, playerStats.getPlaytime());
        statement.setString(11, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }

    public void deletePlayerStats(PlayerStats playerStats) throws SQLException {
        PreparedStatement statement = prepareStatement("DELETE FROM player_stats WHERE uuid = ?");
        statement.setString(1, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }

}

