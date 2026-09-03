package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.automation.config.ConfigReader;

public class LoginPage extends BasePage{

    public By LoginBtn=By.linkText("Login");
    public By username=By.id("userId");
    public By password=By.id("password");
    public By signInBtn=By.id("loginBtn");
    public By bankNavPanel=By.id("bankNav");
    
    public LoginPage(WebDriver driver){
        super(driver);
    }

    public void Login(){

        click(LoginBtn);
        type(username,ConfigReader.getProperty("username"));
        type(password,ConfigReader.getProperty("password"));
        click(signInBtn);
    }

    public boolean dashBoardisDisplayed(){

        wait.until(ExpectedConditions.visibilityOfElementLocated(bankNavPanel));
        return driver.findElement(bankNavPanel).isDisplayed();
    }


}
