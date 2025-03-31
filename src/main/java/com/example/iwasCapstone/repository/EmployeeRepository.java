package com.example.iwasCapstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.iwasCapstone.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByRole(String role);
    List<Employee> findByAvailability(boolean availability);
    @Query(value = """
    SELECT * FROM employee 
    WHERE availability = true 
    AND id NOT IN (SELECT employee_id FROM assignment)
    AND skills && CAST(:skills AS TEXT[])
    """, nativeQuery = true)
List<Employee> findAvailableEmployeesForProject(@Param("skills") String skills);

}
