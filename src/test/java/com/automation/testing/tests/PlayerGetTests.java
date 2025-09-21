package com.automation.testing.tests;

import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.testing.base.BaseTest;
import com.automation.testing.data.PlayerTestData;
import com.automation.testing.data.TestDataProviders;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.dto.response.PlayerGetAllResponseDto;
import com.automation.testing.dto.response.PlayerGetByPlayerIdResponseDto;
import com.automation.testing.dto.response.PlayerItem;
import static com.automation.testing.enums.RoleEnum.SUPERVISOR;
import com.automation.testing.services.PlayerApiService;
import com.automation.testing.utils.JsonSchemaValidator;
import com.automation.testing.utils.ResponseValidator;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Player Management")
@Feature("Player Retrieval")
public class PlayerGetTests extends BaseTest {
    private PlayerApiService playerApiService;

    @BeforeClass
    public void setupClass() {
        playerApiService = new PlayerApiService();
    }

    @Test
    @Description("Test successful player retrieval by valid ID")
    public void testGetPlayerByValidId() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        PlayerGetByPlayerIdResponseDto actual = playerApiService.getPlayerByIdAndParseResponse(
                createdPlayer.getId());

        ResponseValidator.validatePlayerGetResponse(actual, createdPlayer);
    }

    @Test(dataProvider = "invalidPlayerIds", dataProviderClass = TestDataProviders.class)
    @Description("Test player retrieval failure with invalid ID")
    public void testGetPlayerByInvalidId(Long invalidId) {
        Response response = playerApiService.getPlayerByIdExpectingFailure(invalidId, 200);
        ResponseValidator.validateErrorResponse(response, 200);
    }

    @Test
    @Description("Test retrieving all players")
    public void testGetAllPlayers() {
        createTestUserWithSupervisorCredentials();
        createTestUserWithSupervisorCredentials();
        createTestUserWithSupervisorCredentials();

        PlayerGetAllResponseDto allPlayers = playerApiService.getAllPlayersAndParseResponse();

        assertNotNull(allPlayers, "Response should not be null");
        assertNotNull(allPlayers.getPlayers(), "Players list should not be null");
        assertTrue(allPlayers.getPlayers().size() >= 3, "Should have at least 3 players");

        for (PlayerItem player : allPlayers.getPlayers()) {
            assertNotNull(player.getId(), "Player ID should not be null");
            assertNotNull(player.getRole(), "Player role should not be null");
        }
    }

    @Test
    @Description("Test player retrieval response schema validation (Expected to fail due to BUG-001)")
    public void testGetPlayerResponseSchema() {
        PlayerCreateResponseDto createdPlayer = createTestUserWithSupervisorCredentials();

        Response response = playerApiService.getPlayerById(createdPlayer.getId());
        ResponseValidator.validateSuccessResponse(response);

        assertTrue(JsonSchemaValidator.validatePlayerGetResponse(response),
                "Player get response should match the expected JSON schema");
    }

    @Test
    @Description("Test get all players response schema validation")
    public void testGetAllPlayersResponseSchema() {
        createTestUserWithSupervisorCredentials();

        Response response = playerApiService.getAllPlayers();
        ResponseValidator.validateSuccessResponse(response);

        assertTrue(JsonSchemaValidator.validatePlayerGetAllResponse(response),
                "Get all players response should match the expected JSON schema");
    }

    private PlayerCreateResponseDto createTestUserWithSupervisorCredentials() {
        Map<String, Object> requestData = PlayerTestData.validUser();
        return playerApiService.createPlayerWithValidation(SUPERVISOR.getValue(), requestData);
    }
}