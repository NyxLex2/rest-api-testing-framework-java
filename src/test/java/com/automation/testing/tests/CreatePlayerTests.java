package com.automation.testing.tests;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.testing.base.BaseTest;
import com.automation.testing.builders.PlayerCreateRequestBuilder;
import com.automation.testing.data.PlayerTestData;
import com.automation.testing.data.TestDataProviders;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;

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
@Feature("Player Creation")
public class CreatePlayerTests extends BaseTest {
    private PlayerApiService playerApiService;

    @BeforeClass
    public void setupClass() {
        playerApiService = new PlayerApiService();
    }

    @Test
    @Description("Test player creation with valid data - comprehensive validation")
    public void createPlayerWithValidDataTest() {
        Map<String, Object> requestData = PlayerTestData.validUser();

        PlayerCreateResponseDto actual = playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(),
                requestData);

        assertNotNull(actual.getId(), "Player ID should be generated");
        assertTrue(actual.getId() > 0, "Player ID should be positive");
    }

    @Test(dataProvider = "validRoles", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation with valid role - comprehensive validation")
    public void createPlayerWithValidRoleTest(RoleEnum roleEnum) {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withRole(roleEnum)
                .build();

        PlayerCreateResponseDto actual = playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(),
                requestData);
        assertNotNull(actual.getId(), "Player ID should be generated");
    }

    @Test
    @Description("Test that supervisor cannot create another supervisor (Known Business Rule)")
    public void createSupervisorBySupervisorFailsTest() {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withRole(SUPERVISOR)
                .build();

        playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
    }

    @Test(dataProvider = "validGenders", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation with valid gender")
    public void createPlayerWithValidGenderTest(GenderEnum genderEnum) {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withGender(genderEnum)
                .build();

        Response response = playerApiService.createPlayer(SUPERVISOR.getValue(), requestData);
        PlayerCreateResponseDto actual = playerApiService.parsePlayerCreateResponse(response);

        ResponseValidator.validatePlayerCreateResponse(actual, requestData);
    }

    @Test(dataProvider = "boundaryAges", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation with boundary age values - comprehensive validation")
    public void createPlayerWithBoundaryAgeTest(Integer age) {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withAge(age)
                .build();

        PlayerCreateResponseDto actual = playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(),
                requestData);
        assertNotNull(actual.getId(), "Player ID should be generated");
    }

    @Test
    @Description("Test player creation with invalid age below minimum")
    public void createPlayerWithInvalidAgeBelowMinimumTest() {
        Map<String, Object> requestData = PlayerTestData.invalidAgeTooLow();
        ResponseValidator.validateErrorResponse(
                playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400));
    }

    @Test
    @Description("Test player creation with invalid age above maximum")
    public void createPlayerWithInvalidAgeAboveMaximumTest() {
        Map<String, Object> requestData = PlayerTestData.invalidAgeTooHigh();
        ResponseValidator.validateErrorResponse(
                playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400));
    }

    @Test(dataProvider = "invalidPasswords", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation failure with invalid password (BUG-003)")
    public void createPlayerWithInvalidPasswordTest(String invalidPassword) {
        Map<String, Object> requestData = PlayerTestData.validUser();
        requestData.put("password", invalidPassword);

        Response response = playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "invalidGenders", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation failure with invalid gender")
    public void createPlayerWithInvalidGenderTest(String invalidGender) {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withGender(invalidGender)
                .build();

        Response response = playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test(dataProvider = "invalidRoles", dataProviderClass = TestDataProviders.class)
    @Description("Test player creation failure with invalid role")
    public void createPlayerWithInvalidRoleTest(String invalidRole) {
        Map<String, Object> requestData = new PlayerCreateRequestBuilder()
                .withRole(invalidRole)
                .build();

        Response response = playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test
    @Description("Test player creation failure with a missing login field")
    public void createPlayerWithMissingLoginFailsTest() {
        Map<String, Object> requestData = PlayerTestData.validUser();
        requestData.remove("login");

        Response response = playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test
    @Description("Test player creation failure with an empty request body")
    public void createPlayerWithEmptyBodyFailsTest() {
        Map<String, Object> requestData = new HashMap<>();
        Response response = playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), requestData, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test
    @Description("Test that creating a player with a duplicate login fails with 409 Conflict (BUG-004)")
    public void createPlayerWithDuplicateLoginFailsTest() {
        String existingLogin = TestDataGenerator.getUniqueLogin();
        Map<String, Object> initialRequest = PlayerTestData.validUser();
        initialRequest.put("login", existingLogin);
        playerApiService.createPlayer(SUPERVISOR.getValue(), initialRequest);

        Map<String, Object> duplicateRequest = PlayerTestData.validUser();
        duplicateRequest.put("login", existingLogin);
        playerApiService.createPlayerExpectingFailure(SUPERVISOR.getValue(), duplicateRequest, 409);
    }

    @Test
    @Description("Test player creation response schema validation (Expected to fail due to BUG-001)")
    public void createPlayerResponseSchemaTest() {
        Map<String, Object> requestData = PlayerTestData.validUser();

        Response response = playerApiService.createPlayer(SUPERVISOR.getValue(), requestData);
        ResponseValidator.validateSuccessResponse(response);

        assertTrue(JsonSchemaValidator.validatePlayerCreateResponse(response),
                "Player creation response should match the expected JSON schema");
    }
}
