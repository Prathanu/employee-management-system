package com.company.ems.service;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.entity.Employee;
import com.company.ems.exception.DuplicateEmailException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Employee business logic layer — sits between Controller and Repository.
 * @Transactional ensures database operations are atomic.
 */
@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse addEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        Employee employee = mapToEntity(new Employee(), request);
        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String keyword) {
        List<Employee> employees = (keyword == null || keyword.isBlank())
                ? employeeRepository.findAll()
                : employeeRepository.searchByKeyword(keyword.trim());
        return employees.stream().map(EmployeeResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponse::fromEntity)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.findByEmail(request.getEmail())
                .filter(e -> !e.getId().equals(id))
                .ifPresent(e -> { throw new DuplicateEmailException(request.getEmail()); });

        mapToEntity(employee, request);
        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee mapToEntity(Employee employee, EmployeeRequest request) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setJobTitle(request.getJobTitle());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        return employee;
    }
}
