package com.company.ems.tests;

import com.company.ems.pages.DashboardPage;
import com.company.ems.pages.EmployeesPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Dashboard smoke tests — post-login overview and navigation.
 */
@Epic("Employee Management System")
@Feature("Dashboard Smoke Tests")
public class DashboardSmokeTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        loginAsAdmin();
    }

    @Test(description = "Dashboard displays welcome message")
    @Description("Verify dashboard heading and welcome text after login")
    @Story("Dashboard Overview")
    @Severity(SeverityLevel.CRITICAL)
    public void testDashboardDisplaysWelcomeMessage() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isLoaded(), "Dashboard welcome content should be visible");
    }

    @Test(description = "Dashboard stat cards are visible")
    @Description("Verify Total Employees stat card is rendered")
    public void testDashboardStatCardsVisible() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.areStatCardsVisible(), "Stat cards should be visible");
    }

    @Test(description = "Navigate to Employees from sidebar")
    @Description("Click Employees nav link and verify employees page loads")
    public void testNavigateToEmployeesFromSidebar() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.navigateToEmployeesFromSidebar();

        EmployeesPage employeesPage = new EmployeesPage(driver);
        Assert.assertTrue(employeesPage.isLoaded(), "Employees page should load from sidebar navigation");
    }
}
