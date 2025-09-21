package com.automation.testing.builders;

import java.util.HashMap;
import java.util.Map;

import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;
import com.automation.testing.utils.TestDataGenerator;

public class PlayerCreateRequestBuilder {
    private final Map<String, Object> requestBody;

    public PlayerCreateRequestBuilder() {
        requestBody = new HashMap<>();
        withLogin(TestDataGenerator.getUniqueLogin());
        withPassword(TestDataGenerator.getValidPassword());
        withScreenName(TestDataGenerator.getUniqueScreenName());
        withGender(GenderEnum.MALE.getValue());
        withAge(TestDataGenerator.getRandomAge(16, 60));
        withRole(RoleEnum.USER.getValue());
    }

    public PlayerCreateRequestBuilder withLogin(String login) {
        requestBody.put("login", login);
        return this;
    }

    public PlayerCreateRequestBuilder withPassword(String password) {
        requestBody.put("password", password);
        return this;
    }

    public PlayerCreateRequestBuilder withScreenName(String screenName) {
        requestBody.put("screenName", screenName);
        return this;
    }

    public PlayerCreateRequestBuilder withGender(String gender) {
        requestBody.put("gender", gender);
        return this;
    }

    public PlayerCreateRequestBuilder withGender(GenderEnum genderEnum) {
        requestBody.put("gender", genderEnum.getValue());
        return this;
    }

    public PlayerCreateRequestBuilder withAge(Integer age) {
        requestBody.put("age", age);
        return this;
    }

    public PlayerCreateRequestBuilder withRole(String role) {
        requestBody.put("role", role);
        return this;
    }

    public PlayerCreateRequestBuilder withRole(RoleEnum roleEnum) {
        requestBody.put("role", roleEnum.getValue());
        return this;
    }

    public Map<String, Object> build() {
        return new HashMap<>(requestBody);
    }

    public String buildAsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }

        json.append("}");
        return json.toString();
    }

    public static PlayerCreateRequestBuilder validPlayer() {
        return new PlayerCreateRequestBuilder();
    }

    public static PlayerCreateRequestBuilder supervisorPlayer() {
        return new PlayerCreateRequestBuilder()
                .withRole(RoleEnum.SUPERVISOR.getValue())
                .withAge(35)
                .withLogin(TestDataGenerator.getUniqueLogin())
                .withScreenName(TestDataGenerator.getUniqueScreenName());
    }

    public static PlayerCreateRequestBuilder adminPlayer() {
        return new PlayerCreateRequestBuilder()
                .withRole(RoleEnum.ADMIN.getValue())
                .withAge(30)
                .withLogin(TestDataGenerator.getUniqueLogin())
                .withScreenName(TestDataGenerator.getUniqueScreenName());
    }
}
