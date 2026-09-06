package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.automation.config.ConfigReader;

public class LoginPage extends BasePage{

    public By LoginBtn=By.linkText("Login");
    public By usernameField=By.id("userId");
    public By passwordField=By.id("password");
    public By signInBtn=By.id("loginBtn");
    public By bankNavPanel=By.id("bankNav");
    public By errorMsg=By.xpath("//div[@data-testid='errorMsg']");
    
    public LoginPage(WebDriver driver){
        super(driver);
    }

    public LoginPage navigateToLoginPage(){
        click(LoginBtn);
        return this;
    }

    public LoginPage enterUsername(){
        type(usernameField,ConfigReader.getProperty("username"));
        return this;
    }
    public LoginPage enterUsername(String username){
        type(usernameField,username);
        return this;
    }

    public LoginPage enterPassword(){
        type(passwordField,ConfigReader.getProperty("password"));
        return this;
    }
    public LoginPage enterPassword(String password){
        type(passwordField,password);
        return this;
    }

    public LoginPage confirmSignIn(){
        click(signInBtn);
        return this;
    }

    public String getErrorMsg(){
        return getText(errorMsg);
    }

}
