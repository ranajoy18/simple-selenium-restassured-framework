package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TransactionsPage extends BasePage {

    By newestTransactionRow = By.cssSelector("[data-testid='txnRow-0']");

    public TransactionsPage(WebDriver driver) {
        super(driver);
    }

    public String getNewestTransactionText() {
        return getText(newestTransactionRow);
    }

}
