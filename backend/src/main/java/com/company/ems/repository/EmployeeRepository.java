package com.company.ems.repository;

import com.company.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository — auto-implements CRUD methods.
 * Custom @Query for employee search across multiple fields.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT e FROM Employee e
        WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<Employee> searchByKeyword(@Param("keyword") String keyword);
}
