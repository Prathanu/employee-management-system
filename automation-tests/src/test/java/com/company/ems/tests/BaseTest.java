package com.company.ems.tests;

import com.company.ems.config.ConfigReader;
import com.company.ems.config.WebDriverFactory;
import com.company.ems.pages.LoginPage;
import com.company.ems.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base test — WebDriver lifecycle and shared login helper.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = WebDriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            ScreenshotUtil.capture(driver, result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAsAdmin();
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWaitSeconds()))
                .until(ExpectedConditions.urlContains("/dashboard"));
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + System.currentTimeMillis() + "@test.company.com";
    }
}
