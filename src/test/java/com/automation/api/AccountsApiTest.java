package com.automation.api;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.api.model.Account;
import com.automation.config.ConfigReader;

public class AccountsApiTest {

    private final BankingApiClient apiClient = new BankingApiClient();
    private String validToken;

    @BeforeClass
    public void authenticate(){
        validToken = apiClient.getValidToken();
    }

    // The real API returns a single account object at "data" for the authenticated
    // user, not a collection of accounts — there is no multi-account list to page through.
    @Test
    public void getAccountWithValidAuth(){

        Response response = apiClient.getAccount(validToken);

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .body("data.name", not(emptyOrNullString()))
                .body("data.email", equalTo(ConfigReader.getProperty("api.valid.email")))
                .body("data.accountNumber", not(emptyOrNullString()))
                .body("data.balance", notNullValue());
    }

    @Test
    public void getAccountWithoutAuth(){

        Response response = apiClient.getAccount(null);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("error", equalTo("Authorization token is required"));
    }

    @Test
    public void getAccountWithInvalidToken(){

        Response response = apiClient.getAccount("invalid-token");

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("error", equalTo("Invalid or expired token"));
    }

    @Test
    public void accountDataFieldsAreValid(){

        Response response = apiClient.getAccount(validToken);

        String accountNumber = response.jsonPath().getString("data.accountNumber");
        BigDecimal balance = response.jsonPath().getObject("data.balance", BigDecimal.class);

        Assert.assertTrue(accountNumber.matches("ACC-\\d+"), "Account number should match the ACC-#### format");
        Assert.assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0, "Balance should be zero or positive");
    }

    @Test
    public void accountsResponseDeserializesToPojo(){

        Response response = apiClient.getAccount(validToken);
        Account account = response.jsonPath().getObject("data", Account.class);

        Assert.assertEquals(account.getEmail(), ConfigReader.getProperty("api.valid.email"));
        Assert.assertNotNull(account.getId(), "id should deserialize");
        Assert.assertNotNull(account.getAccountNumber(), "accountNumber should deserialize");
        Assert.assertNotNull(account.getBalance(), "balance should deserialize as BigDecimal");
    }

}
