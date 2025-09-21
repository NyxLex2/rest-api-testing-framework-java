package com.automation.testing.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDeleteRequestDto {
    @JsonProperty("playerId")
    private Long playerId;

    public PlayerDeleteRequestDto() {
    }

    public PlayerDeleteRequestDto(Long playerId) {
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
        return "PlayerDeleteRequestDto{" +
                "playerId=" + playerId +
                '}';
    }
}
