package com.example.iwasCapstone.controller;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Assignment;
import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.repository.AllocationRepository;
import com.example.iwasCapstone.repository.EmployeeRepository;
import com.example.iwasCapstone.repository.ProjectRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/allocations") 
public class AllocationController {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    // Existing method to get allocations by project ID
    @GetMapping("/project/{projectId}")
    public List<Assignment> getAllocationsByProject(@PathVariable Long projectId) {  // Use Long instead of Integer
        return allocationRepository.findByProjectId(projectId);
    }
    
    @GetMapping("/employee/{employeeId}")
    public List<Assignment> getAllocationsByEmployee(@PathVariable Long employeeId) {  // Use Long instead of Integer
        return allocationRepository.findByEmployeeId(employeeId);
    }

    @PostMapping("/assign/{employeeId}/{projectId}")
    public ResponseEntity<Assignment> assignEmployeeToProject(
        @PathVariable Long employeeId, 
        @PathVariable Long projectId) {
    
        // Check if employee and project IDs are valid (optional, for better error handling)
        if (employeeRepository.existsById(employeeId) && projectRepository.existsById(projectId)) {
            
            // Check if employee is available
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if (employee != null && !employee.getAvailability()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
            }
    
            Assignment assignment = new Assignment();
            assignment.setEmployeeId(employeeId);
            assignment.setProjectId(projectId);
            assignment.setAssignedDate(LocalDate.now()); 
            assignment.setEndDate(LocalDate.now().plusMonths(1)); 
            Assignment savedAssignment = allocationRepository.save(assignment);
            
            // Update employee availability to false
            if (employee != null) {
                employee.setAvailability(false); // Mark employee as unavailable
                employeeRepository.save(employee);
            }
            
            return ResponseEntity.ok(savedAssignment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
    }
   
}
