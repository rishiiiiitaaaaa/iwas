package com.example.iwasCapstone.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.model.Project;
import com.example.iwasCapstone.repository.EmployeeRepository;
import com.example.iwasCapstone.repository.ProjectRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "*", allowedHeaders = "*") 
@RestController
@RequestMapping("/api")
public class SkillMatchingController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/match/{projectId}")
public ResponseEntity<List<Employee>> suggestEmployeesForProject(@PathVariable Long projectId) {
    Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
    
    List<String> requiredSkills = project.getRequiredSkills();
    if (requiredSkills.isEmpty()) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    // Convert skills list to array format required by PostgreSQL
    String skillsArray = "{" + String.join(",", requiredSkills) + "}";

    List<Employee> employees = employeeRepository.findAvailableEmployeesForProject(skillsArray);
    return ResponseEntity.ok(employees);
}

}
