package com.company.ems.controller;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.exception.DuplicateEmailException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.exception.GlobalExceptionHandler;
import com.company.ems.service.EmployeeService;
import com.company.ems.service.TokenService;
import com.company.ems.support.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private EmployeeService employeeService;
    @MockBean private TokenService tokenService;

    @Test
    void addEmployee_shouldReturn201() throws Exception {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        when(employeeService.addEmployee(any())).thenReturn(EmployeeResponse.fromEntity(TestDataFactory.employeeEntity(1L)));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addEmployee_shouldReturn409_whenDuplicateEmail() throws Exception {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        when(employeeService.addEmployee(any())).thenThrow(new DuplicateEmailException(request.getEmail()));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void searchEmployees_shouldReturn200() throws Exception {
        when(employeeService.searchEmployees("jane"))
                .thenReturn(List.of(EmployeeResponse.fromEntity(TestDataFactory.employeeEntity(1L))));

        mockMvc.perform(get("/api/employees").param("keyword", "jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        when(employeeService.getById(1L)).thenReturn(EmployeeResponse.fromEntity(TestDataFactory.employeeEntity(1L)));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404() throws Exception {
        when(employeeService.getById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_shouldReturn200() throws Exception {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        var response = EmployeeResponse.fromEntity(TestDataFactory.employeeEntity(1L));
        when(employeeService.updateEmployee(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEmployee_shouldReturn204() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_shouldReturn404() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}
