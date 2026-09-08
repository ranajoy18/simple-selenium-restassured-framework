package com.automation.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.automation.pages.BeneficiaryPage;
import com.automation.pages.DashboardPage;

public class BeneficiaryTest extends BaseTest {

    @Test
    public void addNewBeneficiary(){

        String nickname = "AutoQA-" + System.currentTimeMillis();

        DashboardPage dashboardPage = loginAsDefaultUser();
        BeneficiaryPage beneficiaryPage = dashboardPage.navigateToBeneficiaries();

        beneficiaryPage.openAddBeneficiaryForm()
                .fillBeneficiaryDetails("Automation Tester", "1234567890123", "HDFC0001234", nickname);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(beneficiaryPage.getAutoFilledBankName(), "HDFC Bank",
                "Bank name should be auto-filled from the IFSC code");

        beneficiaryPage.submit();
        softAssert.assertTrue(beneficiaryPage.isBeneficiaryListed(nickname),
                "Newly added beneficiary should appear in the beneficiary list");
        softAssert.assertAll();
    }

}
