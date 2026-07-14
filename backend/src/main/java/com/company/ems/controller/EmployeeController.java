package com.company.ems.controller;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Employee REST controller — CRUD endpoints protected by Spring Security.
 * Base path: /api/employees
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /** POST /api/employees — Add new employee */
    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(request));
    }

    /** GET /api/employees?keyword=john — Search or list all employees */
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(employeeService.searchEmployees(keyword));
    }

    /** GET /api/employees/{id} — Get employee by ID */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    /** PUT /api/employees/{id} — Update employee */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    /** DELETE /api/employees/{id} — Delete employee */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
