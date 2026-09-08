package com.automation.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.automation.pages.DashboardPage;
import com.automation.pages.FundTransferPage;

public class FundTransferTest extends BaseTest {

    @Test
    public void verifyTransferPageFormFields(){

        DashboardPage dashboardPage = loginAsDefaultUser();
        FundTransferPage fundTransferPage = dashboardPage.navigateToFundTransfer();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(fundTransferPage.isNeftOptionDisplayed(), "NEFT option should be displayed");
        softAssert.assertTrue(fundTransferPage.isImpsOptionDisplayed(), "IMPS option should be displayed");
        softAssert.assertTrue(fundTransferPage.isRtgsOptionDisplayed(), "RTGS option should be displayed");
        softAssert.assertTrue(fundTransferPage.isStep1ContinueButtonDisplayed(), "Continue button should be displayed");
        softAssert.assertAll();
    }

    @Test
    public void invalidOtpShowsErrorAndBlocksTransfer(){

        DashboardPage dashboardPage = loginAsDefaultUser();
        FundTransferPage fundTransferPage = dashboardPage.navigateToFundTransfer();

        fundTransferPage
                .selectNeft()
                .selectFirstBeneficiary()
                .enterAmount("100")
                .confirmTransfer()
                .enterOtp("000000");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(fundTransferPage.isOtpErrorDisplayed(), "Invalid OTP error message should be displayed");
        softAssert.assertFalse(fundTransferPage.isTransferSuccessful(), "Transfer should not succeed with an invalid OTP");
        softAssert.assertAll();
    }

}
