package com.automation.testing.enums;

public enum RoleEnum {
    SUPERVISOR("supervisor"),
    ADMIN("admin"),
    USER("user");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}


