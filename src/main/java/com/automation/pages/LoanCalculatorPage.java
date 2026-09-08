package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

public class LoanCalculatorPage extends BasePage {

    By amountSlider = By.cssSelector("[data-testid='loan-amount-slider']");
    By rateSlider = By.cssSelector("[data-testid='interest-rate-slider']");
    By tenureSlider = By.cssSelector("[data-testid='tenure-slider']");
    By tenureMonthsBtn = By.cssSelector("[data-testid='tenure-months-btn']");
    By monthlyEmi = By.cssSelector("[data-testid='monthly-emi']");

    public LoanCalculatorPage(WebDriver driver) {
        super(driver);
    }

    @Step("Switch tenure unit to months")
    public LoanCalculatorPage useMonthsForTenure() {
        click(tenureMonthsBtn);
        return this;
    }

    @Step("Set loan amount={amount}, rate={rate}%, tenure={tenureMonths} months")
    public LoanCalculatorPage setLoanInputs(long amount, double rate, int tenureMonths) {
        setSliderValue(amountSlider, String.valueOf(amount));
        setSliderValue(rateSlider, String.valueOf(rate));
        setSliderValue(tenureSlider, String.valueOf(tenureMonths));
        return this;
    }

    public long getMonthlyEmi() {
        String text = getText(monthlyEmi);
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    /** Standard reducing-balance EMI formula, as used by the calculator. */
    public static long expectedEmi(long principal, double annualRatePercent, int tenureMonths) {
        double monthlyRate = annualRatePercent / 12 / 100;
        double factor = Math.pow(1 + monthlyRate, tenureMonths);
        double emi = principal * monthlyRate * factor / (factor - 1);
        return Math.round(emi);
    }

}
