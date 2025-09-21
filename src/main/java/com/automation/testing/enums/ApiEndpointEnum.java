package com.automation.testing.enums;


public enum ApiEndpointEnum {
    PLAYER_CREATE("/player/create/{editor}"),
    PLAYER_DELETE("/player/delete/{editor}"),
    PLAYER_UPDATE("/player/update/{editor}"),
    PLAYER_GET_BY_ID("/player/get"),
    PLAYER_GET_ALL("/player/get/all");

    private final String path;

    ApiEndpointEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
