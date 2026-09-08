package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

public class FixedDepositPage extends BasePage {

    By amountField = By.cssSelector("[data-testid='fdAmount']");
    By submitBtn = By.cssSelector("[data-testid='submitFDBtn']");
    By newestFdCard = By.cssSelector("[data-testid='fdCard-0']");

    public FixedDepositPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open a new Fixed Deposit: amount={amount}, tenure={tenure}")
    public FixedDepositPage openFixedDeposit(String amount, Tenure tenure) {
        type(amountField, amount);
        click(By.cssSelector("[data-testid='" + tenure.testId + "']"));
        click(submitBtn);
        return this;
    }

    public double getLatestFdMaturityAmount() {
        String text = getText(newestFdCard);
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("Maturity: ₹([0-9,]+(?:\\.[0-9]+)?)")
                .matcher(text);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not find maturity amount in FD card: " + text);
        }
        return Double.parseDouble(matcher.group(1).replace(",", ""));
    }

    public enum Tenure {
        SIX_MONTHS("tenure-6mo", 0.5, 5.5),
        ONE_YEAR("tenure-1yr", 1, 6.5),
        TWO_YEARS("tenure-2yr", 2, 6.75),
        THREE_YEARS("tenure-3yr", 3, 7),
        FIVE_YEARS("tenure-5yr", 5, 7.25);

        public final String testId;
        public final double years;
        public final double annualRatePercent;

        Tenure(String testId, double years, double annualRatePercent) {
            this.testId = testId;
            this.years = years;
            this.annualRatePercent = annualRatePercent;
        }

        /** Maturity value for quarterly-compounded interest, as applied by the app. */
        public double expectedMaturity(double principal) {
            double quarterlyRate = annualRatePercent / 100 / 4;
            int quarters = (int) Math.round(years * 4);
            return principal * Math.pow(1 + quarterlyRate, quarters);
        }
    }

}
