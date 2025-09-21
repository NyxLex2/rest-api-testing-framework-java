package com.automation.testing.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PlayerGetByPlayerIdRequestDto {
    @JsonProperty("playerId")
    private Long playerId;

    public PlayerGetByPlayerIdRequestDto() {
    }

    public PlayerGetByPlayerIdRequestDto(Long playerId) {
        this.playerId = playerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "PlayerGetByPlayerIdRequestDto{" +
                "playerId=" + playerId +
                '}';
    }
}
