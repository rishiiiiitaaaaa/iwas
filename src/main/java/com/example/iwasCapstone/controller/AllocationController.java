package com.example.iwasCapstone.controller;

import com.example.iwasCapstone.model.Assignment;
import com.example.iwasCapstone.repository.AllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/allocations")
public class AllocationController {

    @Autowired
    private AllocationRepository allocationRepository;

    @GetMapping("/project/{projectId}")
    public List<Assignment> getAllocationsByProject(@PathVariable Integer projectId) {
        return allocationRepository.findByProjectId(projectId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Assignment> getAllocationsByEmployee(@PathVariable Integer employeeId) {
        return allocationRepository.findByEmployeeId(employeeId);
    }

    @PostMapping("/assign")
    public Assignment assignEmployeeToProject(@RequestBody Assignment assignment) {
        return allocationRepository.save(assignment);
    }

    @DeleteMapping("/unassign/{id}")
    public void unassignEmployeeFromProject(@PathVariable Integer id) {
        allocationRepository.deleteById(id);
    }
}
