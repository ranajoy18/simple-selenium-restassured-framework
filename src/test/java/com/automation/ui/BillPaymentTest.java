package com.automation.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.automation.pages.BillPaymentPage;
import com.automation.pages.DashboardPage;

public class BillPaymentTest extends BaseTest {

    @Test
    public void payElectricityBill(){

        DashboardPage dashboardPage = loginAsDefaultUser();
        BillPaymentPage billPaymentPage = dashboardPage.navigateToBillPayments();

        billPaymentPage
                .selectElectricityCategory()
                .selectProvider("DEWA - Dubai")
                .payBill("CONS123456", "1500");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(billPaymentPage.isPaymentSuccessful(), "Payment success message should be displayed");
        softAssert.assertTrue(billPaymentPage.getSuccessMessage().contains("successful"),
                "Success message should confirm the payment");
        softAssert.assertAll();
    }

}
