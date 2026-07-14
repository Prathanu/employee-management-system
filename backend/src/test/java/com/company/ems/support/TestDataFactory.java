package com.company.ems.support;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.entity.Employee;
import com.company.ems.entity.User;

import java.time.LocalDate;

/** Shared test data for unit and integration tests */
public final class TestDataFactory {

    private TestDataFactory() {}

    public static EmployeeRequest validEmployeeRequest() {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@company.com");
        request.setDepartment("HR");
        request.setJobTitle("HR Manager");
        request.setHireDate(LocalDate.of(2023, 6, 1));
        request.setSalary(65000.0);
        return request;
    }

    public static Employee employeeEntity(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setEmail("jane.smith@company.com");
        employee.setDepartment("HR");
        employee.setJobTitle("HR Manager");
        employee.setHireDate(LocalDate.of(2023, 6, 1));
        employee.setSalary(65000.0);
        return employee;
    }

    public static User adminUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("$2a$10$encodedPasswordHash");
        user.setRole("ADMIN");
        return user;
    }
}
