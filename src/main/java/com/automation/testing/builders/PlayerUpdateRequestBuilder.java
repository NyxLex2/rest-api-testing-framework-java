package com.automation.testing.builders;

import com.automation.testing.dto.request.PlayerUpdateRequestDto;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;
import com.automation.testing.utils.TestDataGenerator;

public class PlayerUpdateRequestBuilder {
    private Integer age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    public PlayerUpdateRequestBuilder() {}

    public PlayerUpdateRequestBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }

    public PlayerUpdateRequestBuilder withGender(GenderEnum genderEnum) {
        this.gender = genderEnum.getValue();
        return this;
    }

    public PlayerUpdateRequestBuilder withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public PlayerUpdateRequestBuilder withLogin(String login) {
        this.login = login;
        return this;
    }

    public PlayerUpdateRequestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public PlayerUpdateRequestBuilder withRole(RoleEnum roleEnum) {
        this.role = roleEnum.getValue();
        return this;
    }

    public PlayerUpdateRequestBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public PlayerUpdateRequestBuilder withScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public PlayerUpdateRequestDto build() {
        return new PlayerUpdateRequestDto(age, gender, login, password, role, screenName);
    }

    public String buildAsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        if (age != null) {
            json.append("\"age\":").append(age).append(",");
        }
        if (gender != null) {
            json.append("\"gender\":\"").append(gender).append("\",");
        }
        if (login != null) {
            json.append("\"login\":\"").append(login).append("\",");
        }
        if (password != null) {
            json.append("\"password\":\"").append(password).append("\",");
        }
        if (role != null) {
            json.append("\"role\":\"").append(role).append("\",");
        }
        if (screenName != null) {
            json.append("\"screenName\":\"").append(screenName).append("\",");
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.setLength(json.length() - 1);
        }

        json.append("}");
        return json.toString();
    }

    public static PlayerUpdateRequestBuilder basicUpdate() {
        return new PlayerUpdateRequestBuilder()
                .withAge(TestDataGenerator.getRandomAge(16, 60))
                .withScreenName(TestDataGenerator.getUniqueScreenName());
    }

    public static PlayerUpdateRequestBuilder basicUpdate(PlayerCreateResponseDto dto) {
        return new PlayerUpdateRequestBuilder()
                .withLogin(dto.getLogin());
    }

    public static PlayerUpdateRequestBuilder roleUpdate(RoleEnum newRoleEnum) {
        return new PlayerUpdateRequestBuilder()
                .withRole(newRoleEnum);
    }

    public static PlayerUpdateRequestBuilder passwordUpdate(String newPassword) {
        return new PlayerUpdateRequestBuilder()
                .withPassword(newPassword);
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getScreenName() {
        return screenName;
    }
}
