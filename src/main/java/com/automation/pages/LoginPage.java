package com.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;
    public By LoginBtn=By.linkText("Login");
    public By username=By.id("userId");
    public By password=By.id("password");
    public By signInBtn=By.id("loginBtn");
    public By bankNavPanel=By.id("bankNav");
    
    public LoginPage(WebDriver driver){
        this.driver=driver;
        this.wait=new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void Login(){

        driver.findElement(LoginBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(username)).sendKeys("rahul@netbank.com");
        driver.findElement(password).sendKeys("Bank@123");
        driver.findElement(signInBtn).click();
    }

    public boolean dashBoardisDisplayed(){

        wait.until(ExpectedConditions.visibilityOfElementLocated(bankNavPanel));
        return driver.findElement(bankNavPanel).isDisplayed();
    }


}
