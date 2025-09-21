package com.automation.testing.base;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automation.testing.utils.AllureReportHelper;
import com.automation.testing.utils.ConfigManager;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public abstract class BaseApiService {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);
    protected final ConfigManager configManager;
    protected final String baseUrl;

    public BaseApiService() {
        this.configManager = ConfigManager.getInstance();
        this.baseUrl = configManager.getBaseUrl();
        setupRestAssuredConfig();
    }

    private void setupRestAssuredConfig() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", configManager.getConnectionTimeout())
                        .setParam("http.socket.timeout", configManager.getRequestTimeout()));

        logger.info("RestAssured configured with base URL: {}", baseUrl);
    }

    protected RequestSpecification createBaseRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured())
                .log().all();
    }


    protected RequestSpecification createAuthenticatedRequestSpec(String authToken) {
        return createBaseRequestSpec()
                .header("Authorization", "Bearer " + authToken);
    }

    protected Response executeGetRequest(String endpoint) {
        logger.info("Executing GET request to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        Response response = requestSpec.get(endpoint);

        logResponseDetails(response, "GET", endpoint);
        AllureReportHelper.attachRequest("GET", baseUrl + endpoint, null);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executeGetRequestWithQueryParams(String endpoint, Map<String, Object> queryParams) {
        logger.info("Executing GET request to: {} with query params: {}", endpoint, queryParams);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (queryParams != null && !queryParams.isEmpty()) {
            requestSpec.queryParams(queryParams);
        }
        Response response = requestSpec.get(endpoint);

        logResponseDetails(response, "GET", endpoint);
        String queryParamsStr = queryParams != null ? queryParams.toString() : "null";
        AllureReportHelper.attachRequest("GET", baseUrl + endpoint, queryParamsStr);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executePostRequest(String endpoint, String requestBody) {
        logger.info("Executing POST request to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (requestBody != null) {
            requestSpec = requestSpec.body(requestBody);
        }

        Response response = requestSpec.post(endpoint);

        logResponseDetails(response, "POST", endpoint);
        AllureReportHelper.attachRequest("POST", baseUrl + endpoint, requestBody);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executePutRequest(String endpoint, String requestBody) {
        logger.info("Executing PUT request to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (requestBody != null) {
            requestSpec = requestSpec.body(requestBody);
        }

        Response response = requestSpec.put(endpoint);

        logResponseDetails(response, "PUT", endpoint);
        AllureReportHelper.attachRequest("PUT", baseUrl + endpoint, requestBody);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executeGetRequestWithBody(String endpoint, String requestBody) {
        logger.info("Executing GET request with body to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (requestBody != null) {
            requestSpec = requestSpec.body(requestBody);
        }

        Response response = requestSpec.get(endpoint);

        logResponseDetails(response, "GET", endpoint);
        AllureReportHelper.attachRequest("GET", baseUrl + endpoint, requestBody);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executePatchRequest(String endpoint, String requestBody) {
        logger.info("Executing PATCH request to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (requestBody != null) {
            requestSpec = requestSpec.body(requestBody);
        }

        Response response = requestSpec.patch(endpoint);

        logResponseDetails(response, "PATCH", endpoint);
        AllureReportHelper.attachRequest("PATCH", baseUrl + endpoint, requestBody);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executeDeleteRequest(String endpoint) {
        logger.info("Executing DELETE request to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        Response response = requestSpec.delete(endpoint);

        logResponseDetails(response, "DELETE", endpoint);
        AllureReportHelper.attachRequest("DELETE", baseUrl + endpoint, null);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    protected Response executeDeleteRequestWithBody(String endpoint, String requestBody) {
        logger.info("Executing DELETE request with body to: {}", endpoint);

        RequestSpecification requestSpec = createBaseRequestSpec();
        if (requestBody != null) {
            requestSpec = requestSpec.body(requestBody);
        }

        Response response = requestSpec.delete(endpoint);

        logResponseDetails(response, "DELETE", endpoint);
        AllureReportHelper.attachRequest("DELETE", baseUrl + endpoint, requestBody);
        AllureReportHelper.attachResponse(response);

        return response;
    }

    private void logResponseDetails(Response response, String method, String endpoint) {
        logger.info("{} {} - Status: {}, Time: {}ms",
                method, endpoint, response.getStatusCode(), response.getTime());

        if (response.getStatusCode() >= 400) {
            logger.warn("Error response body: {}", response.getBody().asString());
        }
    }

    protected void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode != expectedStatusCode) {
            String errorMessage = String.format(
                    "Expected status code %d but got %d. Response: %s",
                    expectedStatusCode, actualStatusCode, response.getBody().asString());
            logger.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }

    protected void validateResponseTime(Response response, long maxResponseTime) {
        long actualResponseTime = response.getTime();
        if (actualResponseTime > maxResponseTime) {
            String errorMessage = String.format(
                    "Response time %dms exceeded maximum allowed %dms",
                    actualResponseTime, maxResponseTime);
            logger.warn(errorMessage);
            AllureReportHelper.addValidationStep("Response Time",
                    maxResponseTime + "ms", actualResponseTime + "ms", false);
        } else {
            AllureReportHelper.addValidationStep("Response Time",
                    "< " + maxResponseTime + "ms", actualResponseTime + "ms", true);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
