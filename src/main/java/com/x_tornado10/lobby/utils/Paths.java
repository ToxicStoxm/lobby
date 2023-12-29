package com.x_tornado10.lobby.utils;

import com.x_tornado10.lobby.Lobby;

import java.util.ArrayList;
import java.util.List;

public class Paths {
    public static void initialize() {
        plPath = Lobby.getInstance().getDataFolder().getPath();
        plFiles.add("milestones.yml");
        plFiles.add("playtime.yml");
    }
    public static String lobby = "Lobby";
    public static String world = "Spawn.World";
    public static String x = "Spawn.X";
    public static String y = "Spawn.Y";
    public static String z = "Spawn.Z";
    public static String yaw = "Spawn.yaw";
    public static String pitch = "Spawn.pitch";
    public static String join_msg = "Join-Message.Actionbar";
    public static String build_mode = "Build-Mode";
    public static String db_host = "Database.host";
    public static  String db_username = "Database.username";
    public static String db_password = "Database.password";
    public static String plPath;
    public static List<String> plFiles = new ArrayList<>();
}