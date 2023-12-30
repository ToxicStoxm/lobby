package com.x_tornado10.lobby.db;

import com.x_tornado10.lobby.playerstats.PlayerStats;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.List;

public class Database {
    private final HikariDataSource dataSource;

    public Database(List<String> credentials) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(credentials.get(0));
        config.setUsername(credentials.get(1));
        config.setPassword(credentials.get(2));
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);
    }

    public void initialize() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "deaths INT, " +
                "kills INT, " +
                "blocks_broken BIGINT, " +
                "blocks_placed BIGINT, " +
                "last_login DATE, " +
                "last_logout DATE, " +
                "logins INT" +
                ")";
        statement.execute(sql);
        statement.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnectionPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        PlayerStats playerStats;

        if(resultSet.next()){

            playerStats = new PlayerStats(resultSet.getString("uuid"), resultSet.getInt("deaths"), resultSet.getInt("kills"), resultSet.getLong("blocks_broken"), resultSet.getLong("blocks_placed"), resultSet.getDate("last_login"), resultSet.getDate("last_logout"), resultSet.getInt("logins"));

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }

    public void createPlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_stats(uuid, deaths, kills, blocks_broken, blocks_placed, last_login, last_logout, logins) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, playerStats.getUuid());
        statement.setInt(2, playerStats.getDeaths());
        statement.setInt(3, playerStats.getKills());
        statement.setLong(4, playerStats.getBlocks_broken());
        statement.setLong(5, playerStats.getBlocks_placed());
        statement.setDate(6, new Date(playerStats.getLast_login().getTime()));
        statement.setDate(7, new Date(playerStats.getLast_logout().getTime()));
        statement.setInt(8, playerStats.getLogins());

        statement.executeUpdate();

        statement.close();

    }

    public void updatePlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("UPDATE player_stats SET deaths = ?, kills = ?, blocks_broken = ?, blocks_placed = ?, last_login = ?, last_logout = ?, logins = ? WHERE uuid = ?");
        statement.setInt(1, playerStats.getDeaths());
        statement.setInt(2, playerStats.getKills());
        statement.setLong(3, playerStats.getBlocks_broken());
        statement.setLong(4, playerStats.getBlocks_placed());
        statement.setDate(5, new Date(playerStats.getLast_login().getTime()));
        statement.setDate(6, new Date(playerStats.getLast_logout().getTime()));
        statement.setInt(7, playerStats.getLogins());
        statement.setString(8, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }

    public void deletePlayerStats(PlayerStats playerStats) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM player_stats WHERE uuid = ?");
        statement.setString(1, playerStats.getUuid());

        statement.executeUpdate();

        statement.close();

    }

}

