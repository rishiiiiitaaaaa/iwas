
package com.example.iwasCapstone.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.repository.EmployeeRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @PostMapping("/add")
public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
    if (employee.getName() == null || employee.getEmail() == null || employee.getRole() == null) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Name, Email, and Role are required"));
    }

    if (employee.getSkills() == null) {
        employee.setSkills(new ArrayList<>()); 
    }

    Employee savedEmployee = employeeRepository.save(employee);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
}


    @GetMapping
    public List<Employee> getallEmployees()
    {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Employee> getEmployeeById(@PathVariable Long id)
    {
        return employeeRepository.findById(id);
    }

    @DeleteMapping("/{id}")
public void deleteEmployee(@PathVariable Long id) {
    employeeRepository.deleteById(id);
}

    @GetMapping("/role/{role}")
    public List<Employee> getEmployeesByRole(@PathVariable String role) {
        return employeeRepository.findByRole(role);
    }
//api for fetching employee by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Employee not found"));
        }
    }
//api for updating employee data 
@PutMapping("/update/{id}")
public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
    Optional<Employee> existingEmployee = employeeRepository.findById(id);
    if (existingEmployee.isPresent()) {
        Employee employee = existingEmployee.get();

        // Update only non-null fields
        if (updatedEmployee.getName() != null) {
            employee.setName(updatedEmployee.getName());
        }
        if (updatedEmployee.getEmail() != null) {
            employee.setEmail(updatedEmployee.getEmail());
        }
        if (updatedEmployee.getSkills() != null) {
            employee.setSkills(updatedEmployee.getSkills());
        } else {
            employee.setSkills(new ArrayList<>()); // Prevent null errors
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(savedEmployee);
    }
    return ResponseEntity.notFound().build();
}

   
}
