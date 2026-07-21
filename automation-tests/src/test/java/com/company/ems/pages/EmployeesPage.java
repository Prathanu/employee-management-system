package com.company.ems.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for /employees — list, search, and CRUD modal.
 */
public class EmployeesPage extends BasePage {

    private static final By PAGE_HEADING = By.xpath("//h2[contains(text(),'Employees')]");
    private static final By ADD_EMPLOYEE_BUTTON = By.xpath("//button[contains(normalize-space(),'Add Employee')]");
    private static final By SEARCH_INPUT = By.cssSelector("input[placeholder*='Search by name']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(normalize-space(),'Search')]");
    private static final By EMPLOYEE_TABLE = By.cssSelector("table.table");
    private static final By LOADING_SPINNER = By.cssSelector(".spinner-border");
    private static final By MODAL_TITLE = By.cssSelector(".modal-title");
    private static final By SUCCESS_ALERT = By.cssSelector(".alert-success");
    private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit' and (contains(normalize-space(),'Add Employee') or contains(normalize-space(),'Update Employee'))]");

    public EmployeesPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(PAGE_HEADING);
    }

    public void waitForTableLoaded() {
        try {
            wait.until(d -> !d.findElements(LOADING_SPINNER).stream().anyMatch(WebElement::isDisplayed));
        } catch (Exception ignored) {
            // Spinner may already be gone
        }
        wait.until(d -> d.findElements(EMPLOYEE_TABLE).size() > 0
                || d.findElements(By.cssSelector(".alert-info")).size() > 0);
    }

    @Step("Click Add Employee button")
    public void clickAddEmployee() {
        click(ADD_EMPLOYEE_BUTTON);
    }

    @Step("Search employees with keyword '{keyword}'")
    public void search(String keyword) {
        type(SEARCH_INPUT, keyword);
        click(SEARCH_BUTTON);
        waitForTableLoaded();
    }

    public void fillEmployeeForm(String firstName, String lastName, String email,
                                 String department, String jobTitle, String hireDate, String salary) {
        setReactInputValue(By.name("firstName"), firstName);
        setReactInputValue(By.name("lastName"), lastName);
        setReactInputValue(By.name("email"), email);
        setReactInputValue(By.name("department"), department);
        setReactInputValue(By.name("jobTitle"), jobTitle);
        setReactInputValue(By.name("hireDate"), hireDate);
        setReactInputValue(By.name("salary"), salary);
    }

    public void submitEmployeeForm() {
        waitUntilEnabled(SUBMIT_BUTTON);
        click(SUBMIT_BUTTON);
    }

    private void setReactInputValue(By locator, String value) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const val = arguments[1];"
                        + "const setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(el, val);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));",
                element, value);
    }

    public boolean waitForEmployeeInTable(String nameFragment) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//table//strong[contains(text(),'" + nameFragment + "')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmployeeInTable(String nameFragment) {
        return !driver.findElements(
                By.xpath("//table//strong[contains(text(),'" + nameFragment + "')]")).isEmpty();
    }

    public boolean isTableVisible() {
        return isDisplayed(EMPLOYEE_TABLE);
    }

    public boolean isModalOpen() {
        return isDisplayed(MODAL_TITLE);
    }

    public void cancelModal() {
        click(By.xpath("//div[contains(@class,'modal')]//button[contains(@class,'btn-close')]"));
    }

    public boolean isFormFieldVisible(String fieldName) {
        return isDisplayed(By.name(fieldName));
    }

    public boolean isEmptyStateOrTableVisible() {
        return isTableVisible() || isDisplayed(By.cssSelector(".alert-info"));
    }

    public boolean waitForSuccessMessageContaining(String text) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_ALERT));
            return alert.getText().contains(text);
        } catch (Exception e) {
            return false;
        }
    }
}
