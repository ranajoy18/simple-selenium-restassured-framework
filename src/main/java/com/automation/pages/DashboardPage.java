package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

public class DashboardPage extends BasePage{

    By welcomeUser = By.cssSelector("[data-testid='welcomeUser']");
    By accountBalance = By.cssSelector("[data-testid='accBal1']");
    By fundTransferNavLink = By.cssSelector("[data-testid='nav-fund-transfer']");
    By beneficiariesNavLink = By.cssSelector("[data-testid='nav-beneficiaries']");
    By billPaymentsNavLink = By.cssSelector("[data-testid='nav-bill-payments']");

     public DashboardPage(WebDriver driver){
        super(driver);
    }

    public boolean isDashboardDisplayed() {
        return isElementDisplayed(welcomeUser);
    }

    public long getAccountBalance() {
        String rawBalance = getText(accountBalance);
        return Long.parseLong(rawBalance.replaceAll("[^0-9]", ""));
    }

    @Step("Navigate to Fund Transfer")
    public FundTransferPage navigateToFundTransfer() {
        click(fundTransferNavLink);
        return new FundTransferPage(driver);
    }

    @Step("Navigate to Beneficiaries")
    public BeneficiaryPage navigateToBeneficiaries() {
        click(beneficiariesNavLink);
        return new BeneficiaryPage(driver);
    }

    @Step("Navigate to Bill Payments")
    public BillPaymentPage navigateToBillPayments() {
        click(billPaymentsNavLink);
        return new BillPaymentPage(driver);
    }

}
