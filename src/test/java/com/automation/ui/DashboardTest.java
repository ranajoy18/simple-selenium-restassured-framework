package com.automation.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.automation.pages.DashboardPage;
import com.automation.pages.FundTransferPage;

public class DashboardTest extends BaseTest {

    @Test
    public void validateBalanceAfterTransfer(){

        long transferAmount = 500;

        DashboardPage dashboardPage = loginAsDefaultUser();
        long balanceBefore = dashboardPage.getAccountBalance();

        FundTransferPage fundTransferPage = dashboardPage.navigateToFundTransfer();

        fundTransferPage
                .selectNeft()
                .selectFirstBeneficiary()
                .enterAmount(String.valueOf(transferAmount))
                .confirmTransfer()
                .enterOtp("123456");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(fundTransferPage.isTransferSuccessful(), "Transfer success message should be displayed");

        long balanceAfter = fundTransferPage.goToDashboard().getAccountBalance();
        softAssert.assertEquals(balanceAfter, balanceBefore - transferAmount,
                "Balance after transfer should be reduced by the transferred amount");
        softAssert.assertAll();
    }

}
