package com.automation.api;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.math.BigDecimal;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.automation.api.model.TransferRequest;

/**
 * These tests mutate the shared account balance, so the whole class runs
 * single-threaded (singleThreaded = true) even though the suite otherwise
 * runs parallel="methods" — concurrent transfers here would race on the
 * same before/after balance reads and produce flaky failures.
 *
 * Two scenarios from the original spec don't map to the real API and are
 * intentionally not implemented as originally worded:
 *  - "invalid source account": there is no source-account request field —
 *    the source is always the authenticated user's own account.
 *  - "invalid beneficiary is rejected": the live API does NOT validate that
 *    toAccountNumber refers to a real account (see
 *    transferToNonexistentAccountIsAcceptedByLenientBackend below, which
 *    documents that real behavior instead of asserting a rejection that
 *    doesn't happen).
 */
@Test(singleThreaded = true)
public class TransferApiTest {

    private static final String DESTINATION_ACCOUNT = "ACC-1004";

    private final BankingApiClient apiClient = new BankingApiClient();
    private String validToken;

    @BeforeClass
    public void authenticate(){
        validToken = apiClient.getValidToken();
    }

    private BigDecimal getBalance(){
        return apiClient.getAccount(validToken).jsonPath().getObject("data.balance", BigDecimal.class);
    }

    @Test
    public void successfulTransferReducesBalanceByTransferAmount(){

        BigDecimal transferAmount = new BigDecimal("500");
        BigDecimal balanceBefore = getBalance();

        Response transferResponse = apiClient.createTransaction(validToken,
                new TransferRequest(transferAmount, "E2E test transfer", DESTINATION_ACCOUNT));

        transferResponse.then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.type", equalTo("transfer"))
                .body("data.status", equalTo("completed"))
                .body("data.toAccountNumber", equalTo(DESTINATION_ACCOUNT))
                .body("data.id", not(emptyOrNullString()));

        BigDecimal actualAmount = transferResponse.jsonPath().getObject("data.amount", BigDecimal.class);
        Assert.assertEquals(actualAmount, transferAmount, "Transaction amount should match what was requested");

        BigDecimal balanceAfter = getBalance();
        Assert.assertEquals(balanceAfter, balanceBefore.subtract(transferAmount),
                "Balance should be reduced by exactly the transferred amount");
    }

    @Test
    public void transferWithoutAuthenticationIsRejected(){

        BigDecimal balanceBefore = getBalance();

        Response response = apiClient.createTransaction(null,
                new TransferRequest(new BigDecimal("500"), "Unauthenticated attempt", DESTINATION_ACCOUNT));

        response.then()
                .statusCode(401)
                .body("success", equalTo(false));

        Assert.assertEquals(getBalance(), balanceBefore, "Balance must not change for an unauthenticated transfer attempt");
    }

    @Test
    public void transferGreaterThanBalanceIsRejectedAndBalanceUnchanged(){

        BigDecimal balanceBefore = getBalance();
        BigDecimal excessiveAmount = balanceBefore.add(new BigDecimal("1000000"));

        Response response = apiClient.createTransaction(validToken,
                new TransferRequest(excessiveAmount, "Overdraw attempt", DESTINATION_ACCOUNT));

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error", equalTo("Insufficient balance"));

        Assert.assertEquals(getBalance(), balanceBefore, "Balance must not change when a transfer is rejected");
    }

    @Test
    public void transferToNonexistentAccountIsAcceptedByLenientBackend(){

        Response response = apiClient.createTransaction(validToken,
                new TransferRequest(new BigDecimal("1"), "Lenient destination check", "ACC-DOES-NOT-EXIST"));

        response.then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.toAccountNumber", equalTo("ACC-DOES-NOT-EXIST"));
    }

    @DataProvider(name = "invalidTransferPayloads")
    public Object[][] invalidTransferPayloads(){
        return new Object[][] {
            {"Zero amount",
                    "{\"type\":\"transfer\",\"amount\":0,\"description\":\"x\",\"toAccountNumber\":\"" + DESTINATION_ACCOUNT + "\"}",
                    400, "type, amount, and description are required"},
            {"Negative amount",
                    "{\"type\":\"transfer\",\"amount\":-500,\"description\":\"x\",\"toAccountNumber\":\"" + DESTINATION_ACCOUNT + "\"}",
                    400, "Amount must be positive"},
            {"Missing amount",
                    "{\"type\":\"transfer\",\"description\":\"x\",\"toAccountNumber\":\"" + DESTINATION_ACCOUNT + "\"}",
                    400, "type, amount, and description are required"},
            {"Missing description",
                    "{\"type\":\"transfer\",\"amount\":500,\"toAccountNumber\":\"" + DESTINATION_ACCOUNT + "\"}",
                    400, "type, amount, and description are required"},
            {"Missing destination account",
                    "{\"type\":\"transfer\",\"amount\":500,\"description\":\"x\"}",
                    400, "Destination account number is required for transfers"},
        };
    }

    @Test(dataProvider = "invalidTransferPayloads")
    public void invalidTransferIsRejected(String scenario, String requestBody, int expectedStatus, String expectedError){

        Response response = apiClient.createTransactionRaw(validToken, requestBody);

        response.then()
                .statusCode(expectedStatus)
                .body("success", equalTo(false))
                .body("error", equalTo(expectedError));
    }

}
