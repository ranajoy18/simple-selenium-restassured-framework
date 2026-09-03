package com.automation.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.automation.pages.DashboardPage;
import com.automation.pages.LoginPage;

public class LoginTest extends BaseTest{

    @Test
    public void validLogin(){

        LoginPage loginPage=new LoginPage(driver);
        DashboardPage dashboardPage =new DashboardPage(driver);
        
        loginPage.Login();
        Assert.assertTrue(dashboardPage.isDashboardDisplayed());    
    }

}
