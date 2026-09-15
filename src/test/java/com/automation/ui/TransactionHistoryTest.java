package com.automation.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.automation.pages.DashboardPage;
import com.automation.pages.FundTransferPage;
import com.automation.pages.TransactionsPage;

public class TransactionHistoryTest extends BaseTest {

    /** End-to-end: login -> fund transfer -> transaction history reflects the transfer. */
    @Test
    public void completedTransferAppearsInTransactionHistory(){

        DashboardPage dashboardPage = loginAsDefaultUser();
        FundTransferPage fundTransferPage = dashboardPage.navigateToFundTransfer();

        fundTransferPage
                .selectNeft()
                .selectFirstBeneficiary()
                .enterAmount("500")
                .confirmTransfer()
                .enterOtp("123456");

        Assert.assertTrue(fundTransferPage.isTransferSuccessful(), "Transfer should succeed before checking history");

        TransactionsPage transactionsPage = fundTransferPage.goToDashboard().navigateToTransactions();
        String newestTransaction = transactionsPage.getNewestTransactionText();

        Assert.assertTrue(newestTransaction.contains("Debit"),
                "Newest transaction should be a debit: " + newestTransaction);
        Assert.assertTrue(newestTransaction.contains("-₹500"),
                "Newest transaction should show the transferred amount: " + newestTransaction);
    }

}
