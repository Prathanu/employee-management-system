package com.company.ems.pages;

import com.company.ems.config.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for /login — authentication form.
 */
public class LoginPage extends BasePage {

    private static final By PAGE_TITLE = By.xpath("//h3[contains(text(),'Employee Management')]");
    private static final By USERNAME_INPUT = By.cssSelector("input[placeholder='Enter username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[placeholder='Enter password']");
    private static final By SIGN_IN_BUTTON = By.xpath("//button[contains(normalize-space(),'Sign In')]");
    private static final By ERROR_ALERT = By.cssSelector(".alert-danger[role='alert']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open login page")
    public LoginPage open() {
        driver.get(ConfigReader.getBaseUrl() + "/login");
        waitForLoginReady();
        return this;
    }

    private void waitForLoginReady() {
        waitForVisible(PAGE_TITLE);
        // Wait for async backend health check — Sign In stays disabled until it completes
        waitUntilEnabled(SIGN_IN_BUTTON);
    }

    public boolean isLoaded() {
        return isDisplayed(PAGE_TITLE);
    }

    @Step("Login with username '{username}'")
    public void login(String username, String password) {
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        waitUntilEnabled(SIGN_IN_BUTTON);
        click(SIGN_IN_BUTTON);
    }

    public void loginAsAdmin() {
        login(ConfigReader.getUsername(), ConfigReader.getPassword());
    }

    public String getErrorMessage() {
        return getText(ERROR_ALERT);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_ALERT);
    }
}
