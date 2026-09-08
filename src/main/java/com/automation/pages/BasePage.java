package com.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected  WebElement waitForElement(By locator) {

        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        // Selenium's native click re-scrolls the element itself right before
        // dispatching a coordinate-based click, which can land the point behind
        // a sticky header/sidebar on longer pages. A JS click sidesteps that by
        // dispatching the click event directly instead of relying on coordinates.
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
    }

    protected void type(By locator, String value) {

        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected String getText(By locator) {

        return waitForElement(locator).getText();
    }

    protected void setSliderValue(By locator, String value) {

        WebElement slider = waitForElement(locator);
        // React tracks the native input value setter, so setting .value directly
        // via a plain JS assignment doesn't trigger its onChange. Going through
        // the native setter and dispatching input/change makes React pick it up.
        String script =
                "var input = arguments[0]; var value = arguments[1];" +
                "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "nativeSetter.call(input, value);" +
                "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                "input.dispatchEvent(new Event('change', { bubbles: true }));";
        ((JavascriptExecutor) driver).executeScript(script, slider, value);
    }

    protected boolean isElementDisplayed(By locator) {

    try {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
    } catch (Exception e) {
        return false;
    }
}
}