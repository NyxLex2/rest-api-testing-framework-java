package com.automation.testing.data;

import java.util.HashMap;
import java.util.Map;

import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;
import com.automation.testing.utils.TestDataGenerator;

public class PlayerTestData {

    public static Map<String, Object> validUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        data.put("gender", GenderEnum.MALE.getValue());
        data.put("login", TestDataGenerator.getUniqueLogin());
        data.put("password", TestDataGenerator.getValidPassword());
        data.put("role", RoleEnum.USER.getValue());
        data.put("screenName", TestDataGenerator.getUniqueScreenName());
        return data;
    }

    public static Map<String, Object> validAdmin() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 30);
        data.put("gender", GenderEnum.FEMALE.getValue());
        data.put("login", TestDataGenerator.getUniqueLogin());
        data.put("password", TestDataGenerator.getValidPassword());
        data.put("role", RoleEnum.ADMIN.getValue());
        data.put("screenName", TestDataGenerator.getUniqueScreenName());
        return data;
    }

    public static Map<String, Object> boundaryMinAge() {
        Map<String, Object> data = validUser();
        data.put("age", 17);
        return data;
    }

    public static Map<String, Object> boundaryMaxAge() {
        Map<String, Object> data = validUser();
        data.put("age", 60);
        return data;
    }

    public static Map<String, Object> invalidAgeTooLow() {
        Map<String, Object> data = validUser();
        data.put("age", 15);
        return data;
    }

    public static Map<String, Object> invalidAgeTooHigh() {
        Map<String, Object> data = validUser();
        data.put("age", 61);
        return data;
    }

    public static Map<String, Object> invalidPassword() {
        Map<String, Object> data = validUser();
        data.put("password", "123");
        return data;
    }

    public static Map<String, Object> invalidGender() {
        Map<String, Object> data = validUser();
        data.put("gender", "invalid");
        return data;
    }

    public static Map<String, Object> invalidRole() {
        Map<String, Object> data = validUser();
        data.put("role", "invalid");
        return data;
    }

    public static Map<String, Object> supervisorRole() {
        Map<String, Object> data = validUser();
        data.put("role", RoleEnum.SUPERVISOR.getValue());
        return data;
    }
}
