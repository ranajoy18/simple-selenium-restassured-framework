package com.automation.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.automation.config.ConfigReader;

public class DriverFactory {
    protected WebDriver driver;

    @BeforeMethod
    public WebDriver setup(){

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver=new ChromeDriver(options);
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
