package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

public class FundTransferPage extends BasePage {

    By neftType = By.cssSelector("[data-testid='type-NEFT']");
    By impsType = By.cssSelector("[data-testid='type-IMPS']");
    By rtgsType = By.cssSelector("[data-testid='type-RTGS']");
    By step1ContinueBtn = By.cssSelector("[data-testid='nextBtn1']");

    By firstBeneficiary = By.cssSelector("[data-testid='bene-BEN001']");
    By step2ContinueBtn = By.cssSelector("[data-testid='nextBtn2']");

    By amountField = By.cssSelector("[data-testid='transferAmount']");
    By step3ContinueBtn = By.cssSelector("[data-testid='nextBtn3']");

    By confirmTransferBtn = By.cssSelector("[data-testid='confirmTransferBtn']");

    By verifyOtpBtn = By.cssSelector("[data-testid='verifyOtpBtn']");

    By successMessage = By.cssSelector("[data-testid='successMessage']");
    By goToDashboardLink = By.cssSelector("[data-testid='goToDashboard']");
    By otpError = By.cssSelector("[data-testid='error-otp']");

    public FundTransferPage(WebDriver driver) {
        super(driver);
    }

    public boolean isNeftOptionDisplayed() {
        return isElementDisplayed(neftType);
    }

    public boolean isImpsOptionDisplayed() {
        return isElementDisplayed(impsType);
    }

    public boolean isRtgsOptionDisplayed() {
        return isElementDisplayed(rtgsType);
    }

    public boolean isStep1ContinueButtonDisplayed() {
        return isElementDisplayed(step1ContinueBtn);
    }

    @Step("Select NEFT transfer type")
    public FundTransferPage selectNeft() {
        click(neftType);
        click(step1ContinueBtn);
        return this;
    }

    @Step("Select saved beneficiary")
    public FundTransferPage selectFirstBeneficiary() {
        click(firstBeneficiary);
        click(step2ContinueBtn);
        return this;
    }

    @Step("Enter transfer amount: {amount}")
    public FundTransferPage enterAmount(String amount) {
        type(amountField, amount);
        click(step3ContinueBtn);
        return this;
    }

    @Step("Confirm transfer details")
    public FundTransferPage confirmTransfer() {
        click(confirmTransferBtn);
        return this;
    }

    @Step("Enter OTP: {otp}")
    public FundTransferPage enterOtp(String otp) {
        // The practice site's mock backend always accepts "123456" as a stand-in
        // for the SMS OTP it can't actually deliver in an automated environment.
        for (int i = 0; i < otp.length(); i++) {
            By otpBox = By.cssSelector("[data-testid='otpBox-" + i + "']");
            type(otpBox, String.valueOf(otp.charAt(i)));
        }
        click(verifyOtpBtn);
        return this;
    }

    public boolean isTransferSuccessful() {
        return isElementDisplayed(successMessage);
    }

    public boolean isOtpErrorDisplayed() {
        return isElementDisplayed(otpError);
    }

    @Step("Return to dashboard")
    public DashboardPage goToDashboard() {
        click(goToDashboardLink);
        return new DashboardPage(driver);
    }

}
