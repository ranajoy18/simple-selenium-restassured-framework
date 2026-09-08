package com.automation.api;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.automation.config.ConfigReader;

public class AuthApiTest {

    private final BankingApiClient apiClient = new BankingApiClient();

    @Test
    public void validLoginReturnsTokenAndUser(){

        Response response = apiClient.login(
                ConfigReader.getProperty("api.valid.email"),
                ConfigReader.getProperty("api.valid.password"));

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("success", equalTo(true))
                .body("data.token", not(emptyOrNullString()))
                .body("data.user.id", notNullValue())
                .body("data.user.name", not(emptyOrNullString()))
                .body("data.user.email", equalTo(ConfigReader.getProperty("api.valid.email")))
                .body("data.user.accountNumber", not(emptyOrNullString()));
    }

    // The API's own documentation (api-testing-guide) describes {"username":...} requests and
    // {"token","user":{"id","name","role"}} responses, but the live API actually expects
    // {"email":...} and wraps everything as {"success","data":{"token","user":{...,"accountNumber"}}}
    // with no "role" field. These payloads/assertions reflect the verified live behavior.
    @DataProvider(name = "invalidLoginPayloads")
    public Object[][] invalidLoginPayloads(){

        String validEmail = ConfigReader.getProperty("api.valid.email");
        String validPassword = ConfigReader.getProperty("api.valid.password");

        return new Object[][] {
            {"Invalid username", "{\"email\":\"wronguser@testerrank.com\",\"password\":\"" + validPassword + "\"}",
                    401, "Invalid email or password"},
            {"Invalid password", "{\"email\":\"" + validEmail + "\",\"password\":\"wrongpassword\"}",
                    401, "Invalid email or password"},
            {"Invalid username and password", "{\"email\":\"wronguser@testerrank.com\",\"password\":\"wrongpassword\"}",
                    401, "Invalid email or password"},
            {"Empty username", "{\"email\":\"\",\"password\":\"" + validPassword + "\"}",
                    400, "Email and password are required"},
            {"Empty password", "{\"email\":\"" + validEmail + "\",\"password\":\"\"}",
                    400, "Email and password are required"},
            {"Missing username", "{\"password\":\"" + validPassword + "\"}",
                    400, "Email and password are required"},
            {"Missing password", "{\"email\":\"" + validEmail + "\"}",
                    400, "Email and password are required"},
            {"Empty request body", "{}",
                    400, "Email and password are required"},
        };
    }

    @Test(dataProvider = "invalidLoginPayloads")
    public void invalidLoginIsRejected(String scenario, String requestBody, int expectedStatus, String expectedError){

        Response response = apiClient.loginRaw(requestBody);

        response.then()
                .statusCode(expectedStatus)
                .contentType("application/json")
                .body("success", equalTo(false))
                .body("error", equalTo(expectedError));
    }

}
