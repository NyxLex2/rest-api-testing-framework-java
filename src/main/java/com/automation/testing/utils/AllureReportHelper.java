package com.automation.testing.utils;

import java.nio.charset.StandardCharsets;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;

public class AllureReportHelper {

    public static void attachRequest(String method, String url, String requestBody) {
        Allure.step("Request: " + method + " " + url);

        if (requestBody != null && !requestBody.isEmpty()) {
            Allure.addAttachment("Request Body", "application/json", requestBody, ".json");
        }

        Allure.addAttachment("Request URL", "text/plain", url, ".txt");
    }

    public static void attachResponse(Response response) {
        if (response != null) {
            String responseBody = response.getBody().asString();
            int statusCode = response.getStatusCode();
            long responseTime = response.getTime();

            Allure.step("Response: " + statusCode + " (" + responseTime + "ms)");

            if (responseBody != null && !responseBody.isEmpty()) {
                String contentType = response.getContentType();
                if (contentType != null && contentType.contains("json")) {
                    Allure.addAttachment("Response Body", "application/json", responseBody, ".json");
                } else {
                    Allure.addAttachment("Response Body", "text/plain", responseBody, ".txt");
                }
            }

            // Add response headers
            String headers = response.getHeaders().toString();
            Allure.addAttachment("Response Headers", "text/plain", headers, ".txt");

            // Add response time as parameter
            Allure.parameter("Response Time (ms)", responseTime);
            Allure.parameter("Status Code", statusCode);
        }
    }

    public static void addStep(String stepName, Status status, String description) {
        Allure.step(stepName, () -> {
            if (description != null && !description.isEmpty()) {
                Allure.addAttachment("Step Details", "text/plain", description, ".txt");
            }
        });
    }

    public static void attachTestData(String testDataName, String testData) {
        if (testData != null && !testData.isEmpty()) {
            Allure.addAttachment(testDataName, "application/json", testData, ".json");
        }
    }

    public static void addValidationStep(String validationName, Object expected, Object actual, boolean passed) {
        String stepName = validationName + (passed ? " ✓" : " ✗");

        Allure.step(stepName, () -> {
            Allure.parameter("Expected", expected);
            Allure.parameter("Actual", actual);

            if (!passed) {
                String details = String.format("Validation failed: Expected '%s' but got '%s'", expected, actual);
                Allure.addAttachment("Validation Details", "text/plain", details, ".txt");
            }
        });
    }

    public static void addEnvironmentInfo(String environment, String baseUrl) {
        Allure.parameter("Environment", environment);
        Allure.parameter("Base URL", baseUrl);
    }

    public static void addAttachment(String name, String type, byte[] content, String fileExtension) {
        Allure.addAttachment(name, type, new String(content, StandardCharsets.UTF_8), fileExtension);
    }

    public static void markTestAsBroken(String reason) {
        Allure.step("Test marked as broken: " + reason, Status.BROKEN);
        Allure.addAttachment("Broken Test Reason", "text/plain", reason, ".txt");
    }

    public static void addFileAttachment(String name, String filePath) {
        try {
            Allure.addAttachment(name, java.nio.file.Files.newInputStream(java.nio.file.Paths.get(filePath)));
        } catch (Exception e) {
            System.err.println("Failed to add file attachment: " + e.getMessage());
        }
    }

    public static void setTestDescription(String description) {
        Allure.description(description);
    }

    public static void addTestStep(String stepName) {
        Allure.step(stepName);
    }
}
