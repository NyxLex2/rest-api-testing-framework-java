package com.automation.testing.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automation.testing.base.BaseApiService;
import com.automation.testing.dto.request.PlayerDeleteRequestDto;
import com.automation.testing.dto.request.PlayerGetByPlayerIdRequestDto;
import com.automation.testing.dto.response.PlayerCreateResponseDto;
import com.automation.testing.dto.response.PlayerGetAllResponseDto;
import com.automation.testing.dto.response.PlayerGetByPlayerIdResponseDto;
import com.automation.testing.dto.response.PlayerUpdateResponseDto;
import com.automation.testing.enums.ApiEndpointEnum;
import com.automation.testing.utils.AllureReportHelper;
import com.automation.testing.utils.ResponseValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class PlayerApiService extends BaseApiService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerApiService.class);
    private final ObjectMapper objectMapper;

    public PlayerApiService() {
        super();
        this.objectMapper = new ObjectMapper();
    }

    @Step("Create player with editor role '{editorRole}' and params: {requestBody}")
    public Response createPlayer(String editorRole, Map<String, Object> requestBody) {
        logger.info("Creating player with editor role '{}' and params: {}", editorRole, requestBody);
        AllureReportHelper.attachTestData("Create Player Request", requestBody.toString());

        // NOTE: This API uses GET method for player creation (non-RESTful design)
        String endpoint = ApiEndpointEnum.PLAYER_CREATE.getPath().replace("{editor}", editorRole);
        Response response = executeGetRequestWithQueryParams(endpoint, requestBody);
        validateResponseTime(response, 5000); // 5 second max response time

        return response;
    }

    @Step("Create player and parse response")
    public PlayerCreateResponseDto createPlayerAndParseResponse(Map<String, Object> requestBody) {
        Response response = createPlayer("supervisor", requestBody);
        return parsePlayerCreateResponse(response);
    }

    @Step("Parse player creation response")
    public PlayerCreateResponseDto parsePlayerCreateResponse(Response response) {
        try {
            validateStatusCode(response, 200);
            String responseBody = response.getBody().asString();
            PlayerCreateResponseDto responseDto = objectMapper.readValue(responseBody, PlayerCreateResponseDto.class);
            logger.info("Successfully created player with ID: {}", responseDto.getId());
            return responseDto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse player creation response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse player creation response", e);
        }
    }

    @Step("Update player {playerId} with editor role '{editorRole}' and request: {requestBody}")
    public Response updatePlayer(String editorRole, Long playerId, String requestBody) {
        logger.info("Updating player {} with editor role '{}' and request body: {}", playerId, editorRole, requestBody);
        AllureReportHelper.attachTestData("Update Player Request", requestBody);

        String endpoint = ApiEndpointEnum.PLAYER_UPDATE.getPath().replace("{editor}", editorRole) + "/" + playerId;
        Response response = executePatchRequest(endpoint, requestBody);
        validateResponseTime(response, 5000);

        return response;
    }

    @Step("Update player {playerId} with request: {requestBody}")
    public Response updatePlayer(Long playerId, String requestBody) {
        return updatePlayer("supervisor", playerId, requestBody);
    }

    @Step("Update player {playerId} with editor role '{editorRole}' and parse response")
    public PlayerUpdateResponseDto updatePlayerAndParseResponse(String editorRole, Long playerId, String requestBody) {
        Response response = updatePlayer(editorRole, playerId, requestBody);
        validateStatusCode(response, 200);

        try {
            PlayerUpdateResponseDto responseDto = objectMapper.readValue(
                    response.getBody().asString(), PlayerUpdateResponseDto.class);
            logger.info("Successfully updated player with ID: {}", responseDto.getId());
            return responseDto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse update player response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse update player response", e);
        }
    }

    @Step("Update player {playerId} and parse response")
    public PlayerUpdateResponseDto updatePlayerAndParseResponse(Long playerId, String requestBody) {
        return updatePlayerAndParseResponse("supervisor", playerId, requestBody);
    }

    @Step("Delete player with ID: {playerId}")
    public Response deletePlayer(Long playerId) {
        logger.info("Deleting player with ID: {}", playerId);

        PlayerDeleteRequestDto deleteRequest = new PlayerDeleteRequestDto(playerId);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(deleteRequest);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize delete request: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize delete request", e);
        }

        AllureReportHelper.attachTestData("Delete Player Request", requestBody);
        String endpoint = ApiEndpointEnum.PLAYER_DELETE.getPath().replace("{editor}", "editor");
        Response response = executeDeleteRequestWithBody(endpoint, requestBody);
        validateResponseTime(response, 3000);

        return response;
    }

    @Step("Delete player {playerId} with editor login '{editorLogin}'")
    public Response deletePlayer(String editorLogin, Long playerId) {
        logger.info("Deleting player {} with editor login '{}'", playerId, editorLogin);

        PlayerDeleteRequestDto deleteRequest = new PlayerDeleteRequestDto(playerId);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(deleteRequest);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize delete request: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize delete request", e);
        }

        AllureReportHelper.attachTestData("Delete Player Request", requestBody);
        String endpoint = ApiEndpointEnum.PLAYER_DELETE.getPath().replace("{editor}", editorLogin);
        Response response = executeDeleteRequestWithBody(endpoint, requestBody);
        validateResponseTime(response, 3000);

        return response;
    }

    @Step("Get player by ID: {playerId}")
    public Response getPlayerById(Long playerId) {
        logger.info("Getting player with ID: {}", playerId);

        PlayerGetByPlayerIdRequestDto getRequest = new PlayerGetByPlayerIdRequestDto(playerId);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(getRequest);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize get request: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize get request", e);
        }

        AllureReportHelper.attachTestData("Get Player Request", requestBody);
        Response response = executePostRequest(ApiEndpointEnum.PLAYER_GET_BY_ID.getPath(), requestBody);
        validateResponseTime(response, 3000);

        return response;
    }

    @Step("Get player by ID {playerId} and parse response")
    public PlayerGetByPlayerIdResponseDto getPlayerByIdAndParseResponse(Long playerId) {
        Response response = getPlayerById(playerId);
        validateStatusCode(response, 200);

        try {
            PlayerGetByPlayerIdResponseDto responseDto = objectMapper.readValue(
                    response.getBody().asString(), PlayerGetByPlayerIdResponseDto.class);
            logger.info("Successfully retrieved player: {}", responseDto.getLogin());
            return responseDto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse get player response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse get player response", e);
        }
    }

    @Step("Get all players")
    public Response getAllPlayers() {
        logger.info("Getting all players");

        Response response = executeGetRequest(ApiEndpointEnum.PLAYER_GET_ALL.getPath());
        validateResponseTime(response, 5000);

        return response;
    }

    @Step("Get all players and parse response")
    public PlayerGetAllResponseDto getAllPlayersAndParseResponse() {
        Response response = getAllPlayers();
        validateStatusCode(response, 200);

        try {
            PlayerGetAllResponseDto responseDto = objectMapper.readValue(
                    response.getBody().asString(), PlayerGetAllResponseDto.class);
            logger.info("Successfully retrieved {} players",
                    responseDto.getPlayers() != null ? responseDto.getPlayers().size() : 0);
            return responseDto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse get all players response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse get all players response", e);
        }
    }

    @Step("Create player expecting failure with status {expectedStatusCode}")
    public Response createPlayerExpectingFailure(String editorRole, Map<String, Object> requestBody,
                                                 int expectedStatusCode) {
        logger.info("Creating player expecting failure with editor role '{}' and params: {}", editorRole, requestBody);
        AllureReportHelper.attachTestData("Create Player Request (Expecting Failure)", requestBody.toString());

        String endpoint = ApiEndpointEnum.PLAYER_CREATE.getPath().replace("{editor}", editorRole);
        Response response = executeGetRequestWithQueryParams(endpoint, requestBody);
        validateStatusCode(response, expectedStatusCode);

        return response;
    }


    @Step("Update player {playerId} with editor role '{editorRole}' expecting failure with status {expectedStatusCode}")
    public Response updatePlayerExpectingFailure(String editorRole, Long playerId, String requestBody,
                                                 int expectedStatusCode) {
        logger.info("Updating player {} with editor role '{}' expecting failure with status {}: {}",
                playerId, editorRole, expectedStatusCode, requestBody);
        AllureReportHelper.attachTestData("Update Player Request (Expecting Failure)", requestBody);

        String endpoint = ApiEndpointEnum.PLAYER_UPDATE.getPath().replace("{editor}", editorRole) + "/" + playerId;
        Response response = executePatchRequest(endpoint, requestBody);
        validateStatusCode(response, expectedStatusCode);

        return response;
    }

    @Step("Update player {playerId} expecting failure with status {expectedStatusCode}")
    public Response updatePlayerExpectingFailure(Long playerId, String requestBody, int expectedStatusCode) {
        return updatePlayerExpectingFailure("supervisor", playerId, requestBody, expectedStatusCode);
    }

    @Step("Delete player {playerId} with editor login '{editorLogin}' expecting failure with status {expectedStatusCode}")
    public Response deletePlayerExpectingFailure(String editorLogin, Long playerId, int expectedStatusCode) {
        logger.info("Deleting player {} with editor login '{}' expecting failure with status {}", playerId, editorLogin,
                expectedStatusCode);

        PlayerDeleteRequestDto deleteRequest = new PlayerDeleteRequestDto(playerId);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(deleteRequest);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize delete request: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize delete request", e);
        }

        AllureReportHelper.attachTestData("Delete Player Request (Expecting Failure)", requestBody);
        String endpoint = ApiEndpointEnum.PLAYER_DELETE.getPath().replace("{editor}", editorLogin);
        Response response = executeDeleteRequestWithBody(endpoint, requestBody);
        validateResponseTime(response, 3000);
        validateStatusCode(response, expectedStatusCode);

        return response;
    }

    @Step("Get player {playerId} expecting failure with status {expectedStatusCode}")
    public Response getPlayerByIdExpectingFailure(Long playerId, int expectedStatusCode) {
        logger.info("Getting player {} expecting failure with status {}", playerId, expectedStatusCode);

        Response response = getPlayerById(playerId);
        validateStatusCode(response, expectedStatusCode);

        return response;
    }

    @Step("Create player with comprehensive validation - editor role '{editorRole}'")
    public PlayerCreateResponseDto createPlayerWithValidation(String editorRole, Map<String, Object> requestBody) {
        logger.info("Creating player with comprehensive validation - editor role '{}', params: {}", editorRole,
                requestBody);

        Response createResponse = createPlayer(editorRole, requestBody);
        PlayerCreateResponseDto createResult = parsePlayerCreateResponse(createResponse);
        ResponseValidator.validatePlayerCreateResponse(createResult, requestBody);

        PlayerGetByPlayerIdResponseDto persistedPlayer = getPlayerByIdAndParseResponse(createResult.getId());
        ResponseValidator.validatePlayerPersistence(persistedPlayer, requestBody);

        logger.info("Successfully created and validated player ID: {}", createResult.getId());
        return createResult;
    }

    @Step("Create player with comprehensive validation")
    public PlayerCreateResponseDto createPlayerWithValidation(Map<String, Object> requestBody) {
        return createPlayerWithValidation("supervisor", requestBody);
    }


    @Step("Update player {playerId} with comprehensive validation - editor role '{editorRole}'")
    public PlayerUpdateResponseDto updatePlayerWithValidation(String editorRole, Long playerId, String requestBody,
                                                              PlayerCreateResponseDto originalPlayer, Integer newAge, String newScreenName) {
        logger.info("Updating player {} with comprehensive validation - editor role '{}', request: {}",
                playerId, editorRole, requestBody);

        PlayerGetByPlayerIdResponseDto originalState = getPlayerByIdAndParseResponse(playerId);

        PlayerUpdateResponseDto updateResult = updatePlayerAndParseResponse(editorRole, playerId, requestBody);
        ResponseValidator.validatePlayerUpdateResponse(updateResult, originalPlayer, newAge, newScreenName);

        PlayerGetByPlayerIdResponseDto persistedPlayer = getPlayerByIdAndParseResponse(playerId);
        ResponseValidator.validatePlayerUpdatePersistence(persistedPlayer, originalState, newAge, newScreenName);

        logger.info("Successfully updated and validated player ID: {}", playerId);
        return updateResult;
    }

    @Step("Update player {playerId} with comprehensive validation")
    public PlayerUpdateResponseDto updatePlayerWithValidation(Long playerId, String requestBody,
                                                              PlayerCreateResponseDto originalPlayer, Integer newAge, String newScreenName) {
        return updatePlayerWithValidation("supervisor", playerId, requestBody, originalPlayer, newAge, newScreenName);
    }
}
