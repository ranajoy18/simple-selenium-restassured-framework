package com.automation.ui;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.automation.config.ConfigReader;
import com.automation.pages.DashboardPage;
import com.automation.pages.LoginPage;

public class LoginTest extends BaseTest{

    @Test
    public void validLogin(){

        LoginPage loginPage=new LoginPage(getDriver());
        DashboardPage dashboardPage =new DashboardPage(getDriver());

        loginPage
        .navigateToLoginPage()
        .enterUsername()
        .enterPassword()
        .confirmSignIn();
        
        Assert.assertTrue(dashboardPage.isDashboardDisplayed());    
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {

    return new Object[][] {
            {"Invalid Password", ConfigReader.getProperty("username"), "wrongpassword","Invalid User ID or Password. Please try again."},
            {"Invalid Username & Password", "wronguser", "wrongpassword","Invalid User ID or Password. Please try again."},
            {"Invalid Username & Password", "wronguser", "wrongpassword","Invalid User ID or Password. Please try again."},
            {"Empty Username", "",ConfigReader.getProperty("username"), "Please enter your User ID and Password."},
            {"Empty Password", ConfigReader.getProperty("username"), "","Please enter your User ID and Password."},
            {"Empty Username & Password", "", "","Please enter your User ID and Password."}
    };
}

    @Test(dataProvider = "invalidLoginData")
    public void invalidLogin(String scenario,String username,String password,String errorMessage){

        LoginPage loginPage=new LoginPage(getDriver());
        DashboardPage dashboardPage =new DashboardPage(getDriver());

        String errorMessageOnUI=loginPage
                            .navigateToLoginPage()
                            .enterUsername(username)
                            .enterPassword(password)
                            .confirmSignIn().getErrorMsg();

        System.out.print("errror Message for scenario - "+scenario+" is as following :: "+errorMessageOnUI);
        Assert.assertEquals(errorMessageOnUI, errorMessage);

        Assert.assertFalse(dashboardPage.isDashboardDisplayed(),"Dashboard should not be displayed: " + scenario);
    }



}
