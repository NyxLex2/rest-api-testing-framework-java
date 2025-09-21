package com.automation.testing.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerGetAllResponseDto {
    @JsonProperty("players")
    private List<PlayerItem> players;

    public PlayerGetAllResponseDto() {
    }

    public PlayerGetAllResponseDto(List<PlayerItem> players) {
        this.players = players;
    }

    public List<PlayerItem> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerItem> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "PlayerGetAllResponseDto{" +
                "players=" + players +
                '}';
    }
}
