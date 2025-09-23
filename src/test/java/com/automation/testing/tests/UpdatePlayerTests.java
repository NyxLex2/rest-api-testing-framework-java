package com.automation.testing.tests;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.testing.base.BaseTest;
import com.automation.testing.builders.PlayerUpdateRequestBuilder;
import com.automation.testing.data.PlayerTestData;
import com.automation.testing.data.TestDataProviders;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.dto.response.PlayerUpdateResponseDto;
import com.automation.testing.enums.GenderEnum;

import static com.automation.testing.enums.RoleEnum.SUPERVISOR;

import com.automation.testing.services.PlayerApiService;
import com.automation.testing.utils.JsonSchemaValidator;
import com.automation.testing.utils.ResponseValidator;
import com.automation.testing.utils.TestDataGenerator;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Player Management")
@Feature("Player Update")
public class UpdatePlayerTests extends BaseTest {
    private PlayerApiService playerApiService;

    @BeforeClass
    public void setupClass() {
        playerApiService = new PlayerApiService();
    }

    @Test
    @Description("Test successful player update with valid data - comprehensive validation")
    public void updatePlayerWithValidDataTest() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        Integer newAge = TestDataGenerator.getRandomAge(17, 60);
        String newScreenName = TestDataGenerator.getUniqueScreenName();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withAge(newAge)
                .withScreenName(newScreenName)
                .buildAsJson();

        PlayerUpdateResponseDto actual = playerApiService.updatePlayerWithValidation(
                createdPlayer.getId(), updateRequestBody, createdPlayer, newAge, newScreenName);

        assertNotNull(actual.getId(), "Player ID should be present");
        assertEquals(actual.getId(), createdPlayer.getId(), "Player ID should remain unchanged");
    }

    @Test(dataProvider = "validGenders", dataProviderClass = TestDataProviders.class)
    @Description("Test player update with valid gender - minimal validation only")
    public void updatePlayerWithValidGenderTest(GenderEnum genderEnum) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withGender(genderEnum)
                .buildAsJson();

        PlayerUpdateResponseDto actual = playerApiService.updatePlayerAndParseResponse(
                createdPlayer.getId(), updateRequestBody);

        assertNotNull(actual, "Update response should not be null");
        assertEquals(actual.getId(), createdPlayer.getId(), "Player ID should remain unchanged");
    }

    @Test
    @Description("Test player password update")
    public void updatePlayerPasswordTest() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();
        String newPassword = TestDataGenerator.getValidPassword();

        String passwordUpdateRequest = PlayerUpdateRequestBuilder.passwordUpdate(newPassword)
                .buildAsJson();

        playerApiService.updatePlayerAndParseResponse(createdPlayer.getId(), passwordUpdateRequest);
    }

    @Test
    @Description("Test player update failure with non-existent player ID")
    public void updateNonExistentPlayerTest() {
        Long nonExistentId = 61459L;
        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate().buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(nonExistentId, updateRequestBody, 200);
        ResponseValidator.validateErrorResponse(response, 200);
    }

    @Test(dataProvider = "invalidAges", dataProviderClass = TestDataProviders.class)
    @Description("Test player update failure with invalid age")
    public void updatePlayerWithInvalidAgeTest(Integer invalidAge) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withAge(invalidAge)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "invalidPasswords", dataProviderClass = TestDataProviders.class)
    @Description("Test player update failure with invalid password (BUG-003)")
    public void updatePlayerWithInvalidPasswordTest(String invalidPassword) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.passwordUpdate(invalidPassword)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "invalidGenders", dataProviderClass = TestDataProviders.class)
    @Description("Test player update failure with invalid gender")
    public void updatePlayerWithInvalidGenderTest(String invalidGender) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withGender(invalidGender)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "invalidRoles", dataProviderClass = TestDataProviders.class)
    @Description("Test player update failure with invalid role")
    public void updatePlayerWithInvalidRoleTest(String invalidRole) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withRole(invalidRole)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "boundaryAges", dataProviderClass = TestDataProviders.class)
    @Description("Test player update with boundary age - comprehensive validation")
    public void updatePlayerWithBoundaryAgeTest(Integer age) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();
        String newScreenName = TestDataGenerator.getUniqueScreenName();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withAge(age)
                .withScreenName(newScreenName)
                .buildAsJson();

        PlayerUpdateResponseDto actual = playerApiService.updatePlayerWithValidation(createdPlayer.getId(),
                updateRequestBody, createdPlayer, age, newScreenName);

        assertNotNull(actual.getId(), "Player ID should be present");
        assertEquals(actual.getId(), createdPlayer.getId(), "Player ID should remain unchanged");
    }

    @Test
    @Description("Test player update with empty request body")
    public void updatePlayerWithEmptyBodyFailsTest() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), "{}", 200);
        ResponseValidator.validateErrorResponse(response, 200);
    }

    @Test
    @Description("Test player update response schema validation (Expected to fail due to BUG-001)")
    public void updatePlayerResponseSchemaTest() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withScreenName(TestDataGenerator.getUniqueScreenName())
                .buildAsJson();

        Response response = playerApiService.updatePlayer(createdPlayer.getId(), updateRequestBody);
        ResponseValidator.validateSuccessResponse(response);

        assertTrue(JsonSchemaValidator.validatePlayerUpdateResponse(response),
                "Player update response should match the expected JSON schema");
    }

    private PlayerCreateResponseDto createTestUserWithSupervisorCredentials() {
        Map<String, Object> requestData = PlayerTestData.validUser();
        return playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(), requestData);
    }
}