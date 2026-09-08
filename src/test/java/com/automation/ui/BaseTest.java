package com.automation.ui;

import com.automation.driver.DriverFactory;
import com.automation.pages.DashboardPage;
import com.automation.pages.LoginPage;

public class BaseTest extends DriverFactory{

    protected DashboardPage loginAsDefaultUser(){
        new LoginPage(getDriver())
                .navigateToLoginPage()
                .enterUsername()
                .enterPassword()
                .confirmSignIn();
        return new DashboardPage(getDriver());
    }

}
