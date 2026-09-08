package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import io.qameta.allure.Step;

public class BillPaymentPage extends BasePage {

    By electricityCategory = By.cssSelector("[data-testid='category-Electricity']");
    By providerSelect = By.cssSelector("[data-testid='billerProvider']");
    By consumerNumberField = By.cssSelector("[data-testid='consumerNumber']");
    By billAmountField = By.cssSelector("[data-testid='billAmount']");
    By payBillBtn = By.cssSelector("[data-testid='payBillBtn']");
    By billSuccessMsg = By.cssSelector("[data-testid='billSuccessMsg']");

    public BillPaymentPage(WebDriver driver) {
        super(driver);
    }

    @Step("Select Electricity bill category")
    public BillPaymentPage selectElectricityCategory() {
        click(electricityCategory);
        return this;
    }

    @Step("Select provider: {provider}")
    public BillPaymentPage selectProvider(String provider) {
        new Select(waitForElement(providerSelect)).selectByVisibleText(provider);
        return this;
    }

    @Step("Pay electricity bill")
    public BillPaymentPage payBill(String consumerNumber, String amount) {
        type(consumerNumberField, consumerNumber);
        type(billAmountField, amount);
        click(payBillBtn);
        return this;
    }

    public boolean isPaymentSuccessful() {
        return isElementDisplayed(billSuccessMsg);
    }

    public String getSuccessMessage() {
        return getText(billSuccessMsg);
    }

}
