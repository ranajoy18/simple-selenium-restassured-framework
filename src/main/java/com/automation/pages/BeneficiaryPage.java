package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

public class BeneficiaryPage extends BasePage {

    By addBeneficiaryBtn = By.cssSelector("[data-testid='addBeneficiaryBtn']");

    By nameField = By.cssSelector("[data-testid='beneName']");
    By accountNumberField = By.cssSelector("[data-testid='beneAccNo']");
    By confirmAccountNumberField = By.cssSelector("[data-testid='beneConfirmAccNo']");
    By ifscField = By.cssSelector("[data-testid='beneIfsc']");
    By bankNameField = By.cssSelector("[data-testid='beneBankName']");
    By nicknameField = By.cssSelector("[data-testid='beneNickname']");
    By submitBtn = By.cssSelector("[data-testid='submitBeneBtn']");

    By beneficiaryList = By.cssSelector("[data-testid='beneficiary-page']");

    public BeneficiaryPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open add-beneficiary form")
    public BeneficiaryPage openAddBeneficiaryForm() {
        click(addBeneficiaryBtn);
        return this;
    }

    @Step("Fill new beneficiary details")
    public BeneficiaryPage fillBeneficiaryDetails(String name, String accountNumber, String ifsc, String nickname) {
        type(nameField, name);
        type(accountNumberField, accountNumber);
        type(confirmAccountNumberField, accountNumber);
        // Bank name is derived from the IFSC code and the field is read-only.
        type(ifscField, ifsc);
        type(nicknameField, nickname);
        return this;
    }

    @Step("Submit new beneficiary")
    public BeneficiaryPage submit() {
        click(submitBtn);
        return this;
    }

    public boolean isBeneficiaryListed(String nickname) {
        return getText(beneficiaryList).contains(nickname);
    }

    public String getAutoFilledBankName() {
        return waitForElement(bankNameField).getAttribute("value");
    }

}
