package com.company.ems.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots on test failure and attaches to Allure report.
 */
public final class ScreenshotUtil {

    private static final Path SCREENSHOT_DIR = Path.of("screenshots");

    private ScreenshotUtil() {
    }

    public static void capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }

        byte[] bytes = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot - " + testName, "image/png", new ByteArrayInputStream(bytes), ".png");

        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path file = SCREENSHOT_DIR.resolve(testName + "-" + timestamp + ".png");
            Files.write(file, bytes);
        } catch (IOException e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
        }
    }
}
