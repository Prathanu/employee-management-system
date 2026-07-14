package com.company.ems.integration;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.support.TestDataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API integration tests — full Spring context + H2 database.
 * Tests the complete request flow: HTTP → Controller → Service → Repository → DB.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String authToken;
    private Long createdEmployeeId;

    @BeforeAll
    void authenticate() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        authToken = body.get("token").asText();
    }

    @Test @Order(1)
    void login_shouldFail_withWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test @Order(2)
    void employees_shouldReturn403_withoutToken() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isForbidden());
    }

    @Test @Order(3)
    void addEmployee_shouldReturn201() throws Exception {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();

        MvcResult result = mockMvc.perform(post("/api/employees")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane.smith@company.com"))
                .andReturn();

        createdEmployeeId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test @Order(4)
    void searchEmployees_shouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/employees").param("keyword", "jane")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test @Order(5)
    void listEmployees_shouldReturnAll() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test @Order(6)
    void getEmployeeById_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/employees/" + createdEmployeeId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdEmployeeId));
    }

    @Test @Order(7)
    void updateEmployee_shouldReturn200() throws Exception {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        request.setJobTitle("Senior HR Manager");
        request.setSalary(70000.0);

        mockMvc.perform(put("/api/employees/" + createdEmployeeId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Senior HR Manager"));
    }

    @Test @Order(8)
    void deleteEmployee_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/employees/" + createdEmployeeId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }

    @Test @Order(9)
    void getEmployeeById_shouldReturn404_afterDelete() throws Exception {
        mockMvc.perform(get("/api/employees/" + createdEmployeeId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
}
