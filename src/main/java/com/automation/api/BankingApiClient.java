package com.automation.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.automation.api.model.LoginRequest;
import com.automation.config.ConfigReader;

public class BankingApiClient {

    private static final String LOGIN_PATH = "/api/practice/banking/auth/login";
    private static final String ACCOUNTS_PATH = "/api/practice/banking/accounts";

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

    /** Logs in with the configured demo credentials and returns just the JWT. */
    public String getValidToken() {
        return login(ConfigReader.getProperty("api.valid.email"), ConfigReader.getProperty("api.valid.password"))
                .jsonPath().getString("data.token");
    }

    /** token may be null to omit the Authorization header entirely. */
    public Response getAccount(String token) {
        RequestSpecification request = baseRequest();
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        return request.get(ACCOUNTS_PATH);
    }

}
