package com.automation.driver;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.automation.config.ConfigReader;

import io.qameta.allure.Allure;

public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver(){
        return driverThreadLocal.get();
    }

    @BeforeMethod
    public WebDriver setup(){

        String browser = ConfigReader.getProperty("browser");
        logger.info("Starting {} session", browser);

        WebDriver driver = createDriver(browser);
        driverThreadLocal.set(driver);

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
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                return new FirefoxDriver(firefoxOptions);
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--window-size=1920,1080");
                return new ChromeDriver(chromeOptions);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result){
        WebDriver driver = driverThreadLocal.get();
        if(driver!=null){
            if(result.getStatus()==ITestResult.FAILURE && driver instanceof TakesScreenshot){
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.getLifecycle().addAttachment("Screenshot on failure", "image/png", "png", screenshot);
            }
            driver.quit();
            driverThreadLocal.remove();
            logger.info("Session closed");
        }
    }
}
