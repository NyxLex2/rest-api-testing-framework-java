package com.automation.testing.utils;

import io.restassured.response.Response;

import java.io.InputStream;

public class JsonSchemaValidator {
    private static final String SCHEMA_BASE_PATH = "/schemas/";

    public static boolean validateResponse(Response response, String schemaFileName) {
        try {
            String schemaPath = SCHEMA_BASE_PATH + schemaFileName;
            InputStream schemaStream = JsonSchemaValidator.class.getResourceAsStream(schemaPath);

            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema file not found: " + schemaPath);
            }

            response.then().assertThat()
                    .body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema(schemaStream));
            return true;
        } catch (Exception e) {
            System.err.println("JSON Schema validation failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateResponseWithSchemaString(Response response, String schemaContent) {
        try {
            response.then().assertThat()
                    .body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema(schemaContent));
            return true;
        } catch (Exception e) {
            System.err.println("JSON Schema validation failed: " + e.getMessage());
            return false;
        }
    }

    public static String getSchemaPath(String schemaFileName) {
        return SCHEMA_BASE_PATH + schemaFileName;
    }

    public static boolean validatePlayerCreateResponse(Response response) {
        return validateResponse(response, "player-create-response-schema.json");
    }

    public static boolean validatePlayerUpdateResponse(Response response) {
        return validateResponse(response, "player-update-response-schema.json");
    }

    public static boolean validatePlayerGetResponse(Response response) {
        return validateResponse(response, "player-get-response-schema.json");
    }

    public static boolean validatePlayerGetAllResponse(Response response) {
        return validateResponse(response, "player-get-all-response-schema.json");
    }

    public static boolean validateErrorResponse(Response response) {
        return validateResponse(response, "error-response-schema.json");
    }
}
