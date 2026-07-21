package com.company.ems.listeners;

import com.company.ems.config.ConfigReader;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Writes environment.properties into allure-results before tests run.
 * Shows browser, URLs, and suite info in the Allure report Environment widget.
 */
public class AllureEnvironmentListener implements ISuiteListener {

    private static final Path ALLURE_RESULTS = Path.of("target", "allure-results");

    @Override
    public void onStart(ISuite suite) {
        try {
            Files.createDirectories(ALLURE_RESULTS);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String content = String.join(System.lineSeparator(),
                    "Project=Employee Management System",
                    "Suite=" + suite.getName(),
                    "Browser=" + ConfigReader.getBrowser(),
                    "Base.URL=" + ConfigReader.getBaseUrl(),
                    "API.URL=" + ConfigReader.getApiUrl(),
                    "Headless=" + ConfigReader.isHeadless(),
                    "OS=" + System.getProperty("os.name"),
                    "Java=" + System.getProperty("java.version"),
                    "Timestamp=" + timestamp
            );
            Files.writeString(ALLURE_RESULTS.resolve("environment.properties"), content);
        } catch (IOException e) {
            System.err.println("Could not write Allure environment.properties: " + e.getMessage());
        }
    }
}
