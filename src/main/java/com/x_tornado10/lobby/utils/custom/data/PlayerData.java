package com.x_tornado10.lobby.utils.custom.data;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class PlayerData {
    private UUID uuid;
    private String prefix;
    private String player_name;
    private String suffix;

    public PlayerData(UUID uuid, String prefix, String player_name, String suffix) {
        this.uuid = uuid;
        this.prefix = prefix;
        this.player_name = player_name;
        this.suffix = suffix;
    }

}
