package com.example.iwasCapstone.controller;

import com.example.iwasCapstone.model.Assignment;
import com.example.iwasCapstone.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentRepository.save(assignment));
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return assignmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id) {
        assignmentRepository.deleteById(id);
        return ResponseEntity.ok("Assignment deleted successfully");
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(assignmentRepository.findByEmployeeId(employeeId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(assignmentRepository.findByProjectId(projectId));
    }
}
