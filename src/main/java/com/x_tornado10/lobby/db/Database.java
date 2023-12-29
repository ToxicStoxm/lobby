package com.x_tornado10.lobby.db;

import com.x_tornado10.lobby.Lobby;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class Database {

    private Connection connection;
    private final Logger logger;
    private final List<String> credentials;
    public Database(List<String> credentials) {
        Lobby plugin = Lobby.getInstance();
        logger = plugin.getLogger();
        this.credentials = credentials;
    }

    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        String url = credentials.get(0);
        String user = credentials.get(1);
        String password = credentials.get(2);

        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        //Create the player_stats table
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (uuid varchar(36) primary key, deaths int, kills int, blocks_broken long, blocks_placed long, last_login DATE, last_logout DATE, logins int)";

        statement.execute(sql);

        statement.close();

    }

}
