package com.company.ems.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;

/**
 * Creates and configures WebDriver instances.
 * WebDriverManager auto-downloads matching browser drivers.
 */
public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox();
            case "edge" -> createEdge();
            default -> createChrome();
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWaitSeconds()));
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createChrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        applyChromiumOptions(options,
                ConfigReader.getBrowserBinary(),
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigReader.isHeadless()) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        applyChromiumOptions(options,
                ConfigReader.getBrowserBinary(),
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe");
        return new EdgeDriver(options);
    }

    private static void applyChromiumOptions(org.openqa.selenium.chromium.ChromiumOptions<?> options,
                                             String configuredBinary,
                                             String... defaultPaths) {
        if (configuredBinary != null && !configuredBinary.isBlank()) {
            options.setBinary(configuredBinary);
        } else {
            for (String path : defaultPaths) {
                if (new File(path).exists()) {
                    options.setBinary(path);
                    break;
                }
            }
        }
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
    }
}
