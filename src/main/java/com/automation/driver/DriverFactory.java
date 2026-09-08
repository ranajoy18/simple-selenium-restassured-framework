package com.automation.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.automation.config.ConfigReader;

public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    protected WebDriver getDriver(){
        return driverThreadLocal.get();
    }

    @BeforeMethod
    public WebDriver setup(){

        String browser = ConfigReader.getProperty("browser");
        logger.info("Starting {} session", browser);

        WebDriver driver = createDriver(browser);
        driverThreadLocal.set(driver);

        driver.manage().window().maximize();
        driver.get(ConfigReader.getProperty("base.url"));
        return driver;
    }

    private WebDriver createDriver(String browser){

        if(browser == null){
            browser = "chrome";
        }

        switch (browser.toLowerCase()){
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                return new FirefoxDriver(firefoxOptions);
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                return new ChromeDriver(chromeOptions);
        }
    }

    @AfterMethod
    public void tearDown(){
        WebDriver driver = driverThreadLocal.get();
        if(driver!=null){
            driver.quit();
            driverThreadLocal.remove();
            logger.info("Session closed");
        }
    }
}
