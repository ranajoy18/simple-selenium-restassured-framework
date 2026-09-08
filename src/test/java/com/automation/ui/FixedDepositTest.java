package com.automation.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.automation.pages.DashboardPage;
import com.automation.pages.FixedDepositPage;
import com.automation.pages.FixedDepositPage.Tenure;

public class FixedDepositTest extends BaseTest {

    @Test
    public void createFixedDepositAndVerifyInterestCalculation(){

        double principal = 100000;
        Tenure tenure = Tenure.ONE_YEAR;

        DashboardPage dashboardPage = loginAsDefaultUser();
        FixedDepositPage fixedDepositPage = dashboardPage.navigateToFixedDeposits();

        fixedDepositPage.openFixedDeposit(String.valueOf((long) principal), tenure);

        double expectedMaturity = tenure.expectedMaturity(principal);
        double actualMaturity = fixedDepositPage.getLatestFdMaturityAmount();

        Assert.assertEquals(actualMaturity, expectedMaturity, 0.01,
                "FD maturity amount should match quarterly-compounded interest calculation");
    }

}
