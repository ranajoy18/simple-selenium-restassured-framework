package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage{

    By welcomeUser = By.cssSelector("[data-testid='welcomeUser']");

     public DashboardPage(WebDriver driver){
        super(driver);
    }

    public boolean isDashboardDisplayed() {
        return waitForElement(welcomeUser).isDisplayed();
    }

}
