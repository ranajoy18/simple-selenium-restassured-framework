package com.automation.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.automation.pages.DashboardPage;
import com.automation.pages.LoanCalculatorPage;

public class LoanCalculatorTest extends BaseTest {

    @Test
    public void verifyEmiAcrossDifferentInputCombinations(){

        DashboardPage dashboardPage = loginAsDefaultUser();
        LoanCalculatorPage loanCalculatorPage = dashboardPage.navigateToLoanCalculator();
        loanCalculatorPage.useMonthsForTenure();

        loanCalculatorPage.setLoanInputs(500000, 8.5, 60);
        assertEmiMatches(loanCalculatorPage, 500000, 8.5, 60);

        loanCalculatorPage.setLoanInputs(1000000, 8.5, 60);
        assertEmiMatches(loanCalculatorPage, 1000000, 8.5, 60);

        loanCalculatorPage.setLoanInputs(1000000, 8.5, 120);
        assertEmiMatches(loanCalculatorPage, 1000000, 8.5, 120);
    }

    private void assertEmiMatches(LoanCalculatorPage loanCalculatorPage, long principal, double rate, int tenureMonths){
        long expectedEmi = LoanCalculatorPage.expectedEmi(principal, rate, tenureMonths);
        Assert.assertEquals(loanCalculatorPage.getMonthlyEmi(), expectedEmi,
                "EMI for principal=" + principal + ", rate=" + rate + "%, tenure=" + tenureMonths + " months should match the reducing-balance formula");
    }

}
