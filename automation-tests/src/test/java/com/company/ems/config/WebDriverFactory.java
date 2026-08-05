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
        setupChromeDriver();
        ChromeOptions options = new ChromeOptions();
        applyChromiumOptions(options,
                ConfigReader.getBrowserBinary(),
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        return new ChromeDriver(options);
    }

    private static void setupChromeDriver() {
        String driverPath = findFirstExisting(
                System.getProperty("webdriver.chrome.driver"),
                System.getenv("CHROME_DRIVER_PATH"),
                "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe"
        );
        if (driverPath != null) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        } else {
            WebDriverManager.chromedriver().setup();
        }
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
        setupEdgeDriver();
        EdgeOptions options = new EdgeOptions();
        applyChromiumOptions(options,
                ConfigReader.getBrowserBinary(),
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe");
        return new EdgeDriver(options);
    }

    private static void setupEdgeDriver() {
        String driverPath = findFirstExisting(
                System.getProperty("webdriver.edge.driver"),
                System.getenv("EDGE_DRIVER_PATH"),
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedgedriver.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedgedriver.exe"
        );
        if (driverPath != null) {
            System.setProperty("webdriver.edge.driver", driverPath);
        } else {
            WebDriverManager.edgedriver().setup();
        }
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

    private static String findFirstExisting(String... paths) {
        if (paths == null) {
            return null;
        }
        for (String path : paths) {
            if (path != null && !path.isBlank() && new File(path).exists()) {
                return path;
            }
        }
        return null;
    }
}
