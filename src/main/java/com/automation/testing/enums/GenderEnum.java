package com.automation.testing.enums;

public enum GenderEnum {
    MALE("male"),
    FEMALE("female");

    private final String value;

    GenderEnum(String value) {
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
