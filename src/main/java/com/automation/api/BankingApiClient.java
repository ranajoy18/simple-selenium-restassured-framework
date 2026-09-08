package com.automation.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.automation.api.model.LoginRequest;
import com.automation.config.ConfigReader;

public class BankingApiClient {

    private static final String LOGIN_PATH = "/api/practice/banking/auth/login";

    private RequestSpecification baseRequest() {
        return RestAssured.given()
                .baseUri(ConfigReader.getProperty("api.base.url"))
                .contentType("application/json")
                .accept("application/json");
    }

    public Response login(String email, String password) {
        return baseRequest()
                .body(new LoginRequest(email, password))
                .post(LOGIN_PATH);
    }

    /** For edge cases (missing/empty fields) where the exact JSON shape matters. */
    public Response loginRaw(String jsonBody) {
        return baseRequest()
                .body(jsonBody)
                .post(LOGIN_PATH);
    }

}
