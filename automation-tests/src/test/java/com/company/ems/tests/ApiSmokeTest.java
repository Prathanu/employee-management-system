package com.company.ems.tests;

import com.company.ems.config.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * API smoke tests — verify backend is reachable (no browser required).
 */
@Epic("Employee Management System")
@Feature("API Smoke Tests")
public class ApiSmokeTest {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Test(description = "Backend health endpoint returns UP")
    @Description("Verifies Spring Boot actuator health at /actuator/health")
    @Story("Backend Health")
    @Severity(SeverityLevel.BLOCKER)
    public void testHealthEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ConfigReader.getApiUrl() + "/actuator/health"))
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(response.statusCode(), 200, "Health endpoint should return 200");
        Assert.assertTrue(response.body().contains("UP"), "Health status should be UP");
    }

    @Test(description = "Login API returns token for valid credentials")
    @Description("POST /api/auth/login with admin credentials")
    @Story("API Authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginApiReturnsToken() throws Exception {
        String body = "{\"username\":\"" + ConfigReader.getUsername()
                + "\",\"password\":\"" + ConfigReader.getPassword() + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ConfigReader.getApiUrl() + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(response.statusCode(), 200, "Login API should return 200");
        Assert.assertTrue(response.body().contains("token"), "Response should contain auth token");
    }
}
