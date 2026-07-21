package com.company.ems.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for /dashboard — overview statistics and quick actions.
 */
public class DashboardPage extends BasePage {

    private static final By PAGE_HEADING = By.xpath("//h2[contains(text(),'Dashboard')]");
    private static final By WELCOME_TEXT = By.xpath("//*[contains(text(),'Welcome to the Employee Management System')]");
    private static final By TOTAL_EMPLOYEES_CARD = By.xpath("//*[contains(text(),'Total Employees')]");
    private static final By MANAGE_EMPLOYEES_LINK = By.xpath("//a[contains(normalize-space(),'Manage Employees')]");
    private static final By SIDEBAR_EMPLOYEES_LINK = By.xpath("//a[contains(@class,'nav-link') and contains(normalize-space(),'Employees')]");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(PAGE_HEADING) && isDisplayed(WELCOME_TEXT);
    }

    public boolean areStatCardsVisible() {
        return isDisplayed(TOTAL_EMPLOYEES_CARD);
    }

    public void clickManageEmployees() {
        click(MANAGE_EMPLOYEES_LINK);
    }

    @Step("Navigate to Employees from sidebar")
    public void navigateToEmployeesFromSidebar() {
        click(SIDEBAR_EMPLOYEES_LINK);
    }
}
