package com.automation.testing.tests;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.automation.testing.enums.GenderEnum;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.automation.testing.base.BaseTest;
import com.automation.testing.builders.PlayerUpdateRequestBuilder;
import com.automation.testing.data.PlayerTestData;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.dto.response.PlayerUpdateResponseDto;

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
public class PlayerUpdateTests extends BaseTest {
    private PlayerApiService playerApiService;

    @BeforeClass
    public void setupClass() {
        playerApiService = new PlayerApiService();
    }

    @Test
    @Description("Test successful player update with valid data - comprehensive validation")
    public void testUpdatePlayerWithValidData() {
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

    @DataProvider(name = "validGendersUpdate")
    public Object[][] validGendersUpdateProvider() {
        return new Object[][]{
                {GenderEnum.MALE},
                {GenderEnum.FEMALE}
        };
    }

    @Test(dataProvider = "validGendersUpdate")
    @Description("Test player update with valid gender - minimal validation only")
    public void testUpdatePlayerWithValidGender(GenderEnum genderEnum) {
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
    public void testUpdatePlayerPassword() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();
        String newPassword = TestDataGenerator.getValidPassword();

        String passwordUpdateRequest = PlayerUpdateRequestBuilder.passwordUpdate(newPassword)
                .buildAsJson();

        playerApiService.updatePlayerAndParseResponse(createdPlayer.getId(), passwordUpdateRequest);
    }

    @Test
    @Description("Test player update failure with non-existent player ID")
    public void testUpdateNonExistentPlayer() {
        Long nonExistentId = 61459L;
        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate().buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(nonExistentId, updateRequestBody, 200);
        ResponseValidator.validateErrorResponse(response, 200);
    }

    @DataProvider(name = "invalidAges")
    public Object[][] invalidAgesProvider() {
        return new Object[][]{
                {-1}, {0}, {15}, {61}, {200}
        };
    }

    @Test(dataProvider = "invalidAges")
    @Description("Test player update failure with invalid age")
    public void testUpdatePlayerWithInvalidAge(Integer invalidAge) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withAge(invalidAge)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @DataProvider(name = "invalidPasswordsUpdate")
    public Object[][] invalidPasswordsUpdateProvider() {
        return new Object[][]{
                {""},
                {"short"},
                {"nouppercase123"},
                {"NOLOWERCASE123"},
                {"NoDigitsHere"}
        };
    }

    @Test(dataProvider = "invalidPasswordsUpdate")
    @Description("Test player update failure with invalid password (BUG-003)")
    public void testUpdatePlayerWithInvalidPassword(String invalidPassword) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.passwordUpdate(invalidPassword)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @DataProvider(name = "invalidGendersUpdate")
    public Object[][] invalidGendersUpdateProvider() {
        return new Object[][]{
                {"unknown"},
                {"other"},
                {"invalid"},
                {""},
                {"123"}
        };
    }

    @Test(dataProvider = "invalidGendersUpdate")
    @Description("Test player update failure with invalid gender")
    public void testUpdatePlayerWithInvalidGender(String invalidGender) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withGender(invalidGender)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @DataProvider(name = "invalidRolesUpdate")
    public Object[][] invalidRolesUpdateProvider() {
        return new Object[][]{
                {"manager"},
                {"guest"},
                {"moderator"},
                {"invalid"},
                {""},
                {"123"}
        };
    }

    @Test(dataProvider = "invalidRolesUpdate")
    @Description("Test player update failure with invalid role")
    public void testUpdatePlayerWithInvalidRole(String invalidRole) {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        String updateRequestBody = PlayerUpdateRequestBuilder.basicUpdate()
                .withRole(invalidRole)
                .buildAsJson();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), updateRequestBody, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @DataProvider(name = "boundaryAgesUpdate")
    public Object[][] boundaryAgesUpdateProvider() {
        return new Object[][]{
                {17},
                {18},
                {59},
                {60}
        };
    }

    @Test(dataProvider = "boundaryAgesUpdate")
    @Description("Test player update with boundary age - comprehensive validation")
    public void testUpdatePlayerWithBoundaryAge(Integer age) {
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
    public void testUpdatePlayerWithEmptyBodyFails() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        Response response = playerApiService.updatePlayerExpectingFailure(
                createdPlayer.getId(), "{}", 200);
        ResponseValidator.validateErrorResponse(response, 200);
    }

    @Test
    @Description("Test player update response schema validation (Expected to fail due to BUG-001)")
    public void testUpdatePlayerResponseSchema() {
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
        // Creates a user player using supervisor credentials for creation
        Map<String, Object> requestData = PlayerTestData.validUser();
        return playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(), requestData);
    }
}