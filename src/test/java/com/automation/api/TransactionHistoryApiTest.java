package com.automation.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.automation.api.model.Transaction;

/**
 * Read-only checks against GET /transactions. Balance/transfer-mutating
 * history assertions (a transfer actually appearing in history, a failed
 * transfer not creating a record) live in TransferApiTest instead, since
 * that class is already single-threaded to safely own the shared account's
 * mutable state — splitting mutation across two parallel-eligible classes
 * would race on the same account.
 */
public class TransactionHistoryApiTest {

    private final BankingApiClient apiClient = new BankingApiClient();
    private String validToken;

    @BeforeClass
    public void authenticate(){
        validToken = apiClient.getValidToken();
    }

    @Test
    public void getTransactionHistoryWithValidAuth(){

        Response response = apiClient.getTransactions(validToken);

        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("success", equalTo(true))
                .body("data.size()", greaterThan(0));
    }

    @Test
    public void getTransactionHistoryWithoutAuth(){

        Response response = apiClient.getTransactions(null);

        // Unlike GET /accounts ("Authorization token is required"), this endpoint
        // returns the same message it uses for an invalid token.
        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("error", equalTo("Invalid or expired token"));
    }

    @Test
    public void transactionHistoryDeserializesToPojoList(){

        Response response = apiClient.getTransactions(validToken);
        List<Transaction> transactions = response.jsonPath().getList("data", Transaction.class);

        Assert.assertFalse(transactions.isEmpty(), "Transaction history should not be empty");
        for (Transaction transaction : transactions) {
            Assert.assertNotNull(transaction.getId(), "id should deserialize");
            Assert.assertNotNull(transaction.getType(), "type should deserialize");
            Assert.assertNotNull(transaction.getAmount(), "amount should deserialize as BigDecimal");
            Assert.assertNotNull(transaction.getStatus(), "status should deserialize");
        }
    }

}
