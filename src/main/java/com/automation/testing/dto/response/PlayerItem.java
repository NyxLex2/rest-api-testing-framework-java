package com.automation.testing.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerItem {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("role")
    private String role;

    @JsonProperty("screenName")
    private String screenName;

    public PlayerItem() {
    }

    public PlayerItem(Long id, Integer age, String gender, String role, String screenName) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.role = role;
        this.screenName = screenName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public String toString() {
        return "PlayerItem{" +
                "id=" + id +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
