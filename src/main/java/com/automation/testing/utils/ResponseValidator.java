package com.automation.testing.utils;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.dto.response.PlayerGetByPlayerIdResponseDto;
import com.automation.testing.dto.response.PlayerUpdateResponseDto;

import io.restassured.response.Response;

public class ResponseValidator {

    public static void validatePlayerCreateResponse(PlayerCreateResponseDto actual, Map<String, Object> requestData) {
        assertNotNull(actual, "Response should not be null");
        assertNotNull(actual.getId(), "Player ID should be generated");
        assertTrue(actual.getId() > 0, "Player ID should be positive");

        if (actual.getLogin() != null) {
            assertEquals(actual.getLogin(), requestData.get("login"), "Login should match when present");
        }
        if (actual.getAge() != null) {
            assertEquals(actual.getAge(), requestData.get("age"), "Age should match when present");
        }
        if (actual.getScreenName() != null) {
            assertEquals(actual.getScreenName(), requestData.get("screenName"),
                    "Screen name should match when present");
        }
        if (actual.getPassword() != null) {
            assertEquals(actual.getPassword(), requestData.get("password"), "Password should match when present");
        }
    }

    public static void validatePlayerUpdateResponse(PlayerUpdateResponseDto actual, PlayerCreateResponseDto original,
                                                    Integer newAge, String newScreenName) {
        assertNotNull(actual, "Update response should not be null");
        assertEquals(actual.getId(), original.getId(), "Player ID should remain unchanged");

        // Only validate fields that are reliably returned in update response
        if (actual.getAge() != null) {
            assertEquals(actual.getAge(), newAge, "Age should match when present");
        }
        if (actual.getScreenName() != null) {
            assertEquals(actual.getScreenName(), newScreenName, "Screen name should match when present");
        }
        if (actual.getLogin() != null) {
            assertEquals(actual.getLogin(), original.getLogin(), "Login should remain unchanged when present");
        }
    }

    public static void validatePlayerGetResponse(PlayerGetByPlayerIdResponseDto actual,
                                                 PlayerCreateResponseDto original) {
        assertNotNull(actual, "Get response should not be null");
        assertEquals(actual.getId(), original.getId(), "Player ID should match");

        PlayerGetByPlayerIdResponseDto expected = new PlayerGetByPlayerIdResponseDto();
        expected.setId(original.getId());
        expected.setAge(original.getAge());
        expected.setGender(original.getGender());
        expected.setLogin(original.getLogin());
        expected.setPassword(original.getPassword());
        expected.setRole(original.getRole());
        expected.setScreenName(original.getScreenName());

        ObjectComparator.assertEqualsIgnoreNull(expected, actual);
    }

    public static void validateErrorResponse(Response response) {
        assertNotNull(response, "Response should not be null");
        assertTrue(response.getStatusCode() >= 400,
                "Error response should have status code >= 400, but got: " + response.getStatusCode());
        JsonSchemaValidator.validateErrorResponse(response);
    }

    public static void validateErrorResponse(Response response, int expectedStatusCode) {
        assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code " + expectedStatusCode + " but got " + response.getStatusCode());
        JsonSchemaValidator.validateErrorResponse(response);
    }

    public static void validateSuccessResponse(Response response) {
        assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300,
                "Response should be successful (2xx)");
    }

    public static void validatePlayerPersistence(PlayerGetByPlayerIdResponseDto actual,
                                                 Map<String, Object> originalRequestData) {
        assertNotNull(actual, "GET response should not be null");
        assertNotNull(actual.getId(), "Player ID should be present");

        assertEquals(actual.getLogin(), originalRequestData.get("login"), "Login should be persisted correctly");
        assertEquals(actual.getAge(), originalRequestData.get("age"), "Age should be persisted correctly");
        assertEquals(actual.getGender(), originalRequestData.get("gender"), "Gender should be persisted correctly");
        assertEquals(actual.getRole(), originalRequestData.get("role"), "Role should be persisted correctly");
        assertEquals(actual.getScreenName(), originalRequestData.get("screenName"),
                "Screen name should be persisted correctly");
    }

    public static void validatePlayerUpdatePersistence(PlayerGetByPlayerIdResponseDto actual,
                                                       PlayerGetByPlayerIdResponseDto originalState,
                                                       Integer newAge, String newScreenName) {
        assertNotNull(actual, "GET response should not be null");
        assertEquals(actual.getId(), originalState.getId(), "Player ID should remain unchanged");

        // Validate updated fields
        assertEquals(actual.getAge(), newAge, "Age should be updated correctly");
        assertEquals(actual.getScreenName(), newScreenName, "Screen name should be updated correctly");

        // Validate unchanged fields
        assertEquals(actual.getLogin(), originalState.getLogin(), "Login should remain unchanged");
        assertEquals(actual.getRole(), originalState.getRole(), "Role should remain unchanged");
        assertEquals(actual.getGender(), originalState.getGender(), "Gender should remain unchanged");
    }
}