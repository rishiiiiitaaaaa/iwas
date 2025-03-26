package com.example.iwasCapstone.controller;

import java.util.List;
import java.util.Optional;

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

import com.example.iwasCapstone.model.LeaveRequest;
import com.example.iwasCapstone.repository.LeaveRequestRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/leaverequests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRequestRepository;
    @GetMapping
public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
    List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
    return ResponseEntity.ok(leaveRequests);
}


    @PostMapping
    public ResponseEntity<LeaveRequest> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.ok(savedLeaveRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);
        return leaveRequest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequestStatus(@PathVariable Long id, @RequestBody String status) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);
        if (leaveRequest.isPresent()) {
            LeaveRequest updatedLeaveRequest = leaveRequest.get();
            updatedLeaveRequest.setStatus(status);
            leaveRequestRepository.save(updatedLeaveRequest);
            return ResponseEntity.ok(updatedLeaveRequest);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeaveRequest(@PathVariable Long id) {
        if (leaveRequestRepository.existsById(id)) {
            leaveRequestRepository.deleteById(id);
            return ResponseEntity.ok("Leave request deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}
