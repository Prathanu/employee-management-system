package com.company.ems.service;

import com.company.ems.dto.EmployeeRequest;
import com.company.ems.exception.DuplicateEmailException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @InjectMocks private EmployeeService employeeService;

    @Test
    void addEmployee_shouldCreate_whenEmailUnique() {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(employeeRepository.save(any())).thenReturn(TestDataFactory.employeeEntity(1L));

        var response = employeeService.addEmployee(request);

        assertEquals(1L, response.getId());
        assertEquals("jane.smith@company.com", response.getEmail());
    }

    @Test
    void addEmployee_shouldThrow_whenEmailExists() {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> employeeService.addEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void searchEmployees_shouldReturnAll_whenKeywordBlank() {
        when(employeeRepository.findAll()).thenReturn(List.of(TestDataFactory.employeeEntity(1L)));

        var results = employeeService.searchEmployees("  ");

        assertEquals(1, results.size());
        verify(employeeRepository).findAll();
    }

    @Test
    void searchEmployees_shouldSearch_whenKeywordProvided() {
        when(employeeRepository.searchByKeyword("jane"))
                .thenReturn(List.of(TestDataFactory.employeeEntity(1L)));

        var results = employeeService.searchEmployees("jane");

        assertEquals(1, results.size());
        verify(employeeRepository).searchByKeyword("jane");
    }

    @Test
    void getById_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(TestDataFactory.employeeEntity(1L)));

        var response = employeeService.getById(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getById(99L));
    }

    @Test
    void updateEmployee_shouldUpdate_whenValid() {
        EmployeeRequest request = TestDataFactory.validEmployeeRequest();
        request.setJobTitle("Senior HR Manager");
        var existing = TestDataFactory.employeeEntity(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);

        var response = employeeService.updateEmployee(1L, request);

        assertEquals("Senior HR Manager", response.getJobTitle());
    }

    @Test
    void deleteEmployee_shouldDelete_whenExists() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldThrow_whenNotFound() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(99L));
    }
}
