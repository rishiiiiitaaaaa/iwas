package com.example.iwasCapstone.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.repository.EmployeeRepository;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Employee employee) {
        if (employee.getName() == null || employee.getEmail() == null || employee.getPassword() == null || employee.getRole() == null) {
            return ResponseEntity.badRequest().body("All fields (name, email, password, role) are required!");
        }
    
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
    
        if (employee.getSkills() == null) {
            employee.setSkills(List.of());  //  Set an empty list instead of null
        }
    
        try {
            employeeRepository.save(employee);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(employee.getEmail());
    
        if (existingEmployee.isPresent() && existingEmployee.get().getPassword().equals(employee.getPassword())) {
            Employee loggedInEmployee = existingEmployee.get();
    
            // Create a response map
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("role", loggedInEmployee.getRole()); // Return user's role
            response.put("email", loggedInEmployee.getEmail()); // Return email for session handling
    
            return ResponseEntity.ok(response);
        }
    
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    

}
