package com.automation.testing.tests;

import java.util.Map;

import com.automation.testing.enums.RoleEnum;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.testing.base.BaseTest;
import com.automation.testing.data.PlayerTestData;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.services.PlayerApiService;
import com.automation.testing.utils.ResponseValidator;
import com.automation.testing.utils.TestDataGenerator;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Player Management")
@Feature("Player Deletion with Editor Login")
public class DeletePlayerTests extends BaseTest {

    private PlayerApiService playerApiService;

    @BeforeClass
    public void setup() {
        playerApiService = new PlayerApiService();
    }

    private PlayerCreateResponseDto createTestPlayerWithSpecificData() {
        Map<String, Object> requestData = PlayerTestData.validUser();
        requestData.put("login", TestDataGenerator.getUniqueLogin());
        requestData.put("password", TestDataGenerator.getValidPassword());
        requestData.put("screenName", TestDataGenerator.getUniqueScreenName());
        return playerApiService.createPlayerWithValidation(RoleEnum.SUPERVISOR.getValue(), requestData);
    }

    private PlayerCreateResponseDto createEditor(RoleEnum roleEnum) {
        Map<String, Object> requestData = PlayerTestData.validUser();
        requestData.put("role", roleEnum.getValue());
        requestData.put("login", TestDataGenerator.getUniqueLogin());
        requestData.put("password", TestDataGenerator.getValidPassword());
        requestData.put("screenName", TestDataGenerator.getUniqueScreenName());
        return playerApiService.createPlayerWithValidation(RoleEnum.SUPERVISOR.getValue(), requestData);
    }

    @Test
    @Description("Test successful deletion of an existing player by supervisor login")
    public void deletePlayerTest() {
        PlayerCreateResponseDto editorPlayer = createEditor(RoleEnum.ADMIN);
        String editorLogin = editorPlayer.getLogin();

        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        Response response = playerApiService.deletePlayer(editorLogin, playerId);

        ResponseValidator.validateSuccessResponse(response);

    }

    @Test
    @Description("Test deletion of non-existent player by supervisor login")
    public void deleteNonExistedPlayerTest() {
        PlayerCreateResponseDto editorPlayer = createEditor(RoleEnum.ADMIN);
        String editorLogin = editorPlayer.getLogin();

        Long nonExistentId = 999999999L;

        Response response = playerApiService.deletePlayerExpectingFailure(editorLogin, nonExistentId, 403);
        ResponseValidator.validateErrorResponse(response, 403);
    }

    @Test
    @Description("Test deletion by user login - should fail with 403")
    public void deletePlayerByUserTest() {
        PlayerCreateResponseDto editorPlayer = createEditor(RoleEnum.USER);
        String editorLogin = editorPlayer.getLogin();

        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        Response response = playerApiService.deletePlayerExpectingFailure(editorLogin, playerId, 403);
        ResponseValidator.validateErrorResponse(response, 403);

        Response getResponse = playerApiService.getPlayerById(playerId);
        ResponseValidator.validateSuccessResponse(getResponse);
    }

    @Test
    @Description("Test deletion with invalid editor login")
    public void deletePlayerWithInvalidEditor() {
        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        String invalidLogin = TestDataGenerator.getUniqueLogin();
        Response response = playerApiService.deletePlayerExpectingFailure(invalidLogin, playerId, 403);
        ResponseValidator.validateErrorResponse(response, 403);

        Response getResponse = playerApiService.getPlayerById(playerId);
        ResponseValidator.validateSuccessResponse(getResponse);
    }

    @Test
    @Description("Test deletion with empty editor login")
    public void deletePlayerWithEmptyEditorTest() {
        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        Response response = playerApiService.deletePlayerExpectingFailure("", playerId, 404);
        ResponseValidator.validateErrorResponse(response, 404);

        Response getResponse = playerApiService.getPlayerById(playerId);
        ResponseValidator.validateSuccessResponse(getResponse);
    }

    @Test
    @Description("Test deletion with case sensitivity in editor login")
    public void deletePlayerWithCaseSensitiveEditorTest() {
        PlayerCreateResponseDto editorPlayer = createEditor(RoleEnum.ADMIN);
        String originalLogin = editorPlayer.getLogin();
        String upperCaseLogin = originalLogin.toUpperCase();

        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        Response response = playerApiService.deletePlayerExpectingFailure(upperCaseLogin, playerId, 403);
        ResponseValidator.validateErrorResponse(response, 403);

        Response getResponse = playerApiService.getPlayerById(playerId);
        ResponseValidator.validateSuccessResponse(getResponse);
    }

    @Test
    @Description("Test deletion with spaces in editor login")
    public void deletePlayerWithSpacesTest() {
        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        String loginWithSpaces = "user with spaces";
        Response response = playerApiService.deletePlayerExpectingFailure(loginWithSpaces, playerId, 403);
        ResponseValidator.validateErrorResponse(response, 403);

        Response getResponse = playerApiService.getPlayerById(playerId);
        ResponseValidator.validateSuccessResponse(getResponse);
    }

    @Test
    @Description("Test deletion with malformed editor login path")
    public void deletePlayerWithMalformedEditorLoginPath() {
        PlayerCreateResponseDto playerToDelete = createTestPlayerWithSpecificData();
        Long playerId = playerToDelete.getId();

        String malformedLogin = "user/../admin";
        Response response = playerApiService.deletePlayerExpectingFailure(malformedLogin, playerId, 400);
        ResponseValidator.validateErrorResponse(response, 400);
    }

    @Test
    @Description("Test deletion with valid editor login but invalid player ID format")
    public void testDeletePlayerWithValidEditorLoginButInvalidPlayerId() {
        PlayerCreateResponseDto editorPlayer = createEditor(RoleEnum.ADMIN);
        String editorLogin = editorPlayer.getLogin();

        Long invalidPlayerId = -1L;
        Response response = playerApiService.deletePlayerExpectingFailure(editorLogin, invalidPlayerId, 403);
        ResponseValidator.validateErrorResponse(response, 403);
    }
}
