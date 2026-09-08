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

}
