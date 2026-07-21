package com.company.ems.tests;

import com.company.ems.config.ConfigReader;
import com.company.ems.pages.DashboardPage;
import com.company.ems.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Login page smoke tests — UI authentication flows.
 */
@Epic("Employee Management System")
@Feature("Login Smoke Tests")
public class LoginSmokeTest extends BaseTest {

    @Test(description = "Login page loads with title and form")
    @Description("Navigate to /login and verify page elements are visible")
    public void testLoginPageLoads() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        Assert.assertTrue(loginPage.isLoaded(), "Login page title should be visible");
    }

    @Test(description = "Valid credentials redirect to dashboard")
    @Description("Login with admin/admin123 and verify dashboard loads")
    @Story("UI Authentication")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLoginRedirectsToDashboard() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(ConfigReader.getUsername(), ConfigReader.getPassword());

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard should load after successful login");
    }

    @Test(description = "Invalid credentials show error message")
    @Description("Login with wrong password and verify error alert is displayed")
    public void testInvalidLoginShowsError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(ConfigReader.getUsername(), "wrong-password");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error alert should be displayed");
    }
}
