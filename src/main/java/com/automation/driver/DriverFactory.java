package com.automation.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.automation.config.ConfigReader;

public class DriverFactory {
    protected WebDriver driver;

    @BeforeMethod
    public WebDriver setup(){

        driver=new ChromeDriver();
        driver.manage().window().maximize();

        driver.get(ConfigReader.getProperty("base.url"));
        return driver;
    }

    @AfterMethod
    public void tearDown(){
        if(driver!=null){
            driver.quit();
        }
    }
}
