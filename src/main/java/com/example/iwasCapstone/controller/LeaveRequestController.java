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

import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.model.LeaveRequest;
import com.example.iwasCapstone.model.LeaveStatus;
import com.example.iwasCapstone.repository.EmployeeRepository;
import com.example.iwasCapstone.repository.LeaveRequestRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/leaverequests")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    //get all leave request 
    @GetMapping
public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
    List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
    return ResponseEntity.ok(leaveRequests);
}

//create a leave request 
    @PostMapping
    public ResponseEntity<LeaveRequest> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.ok(savedLeaveRequest);
    }

//get leave request by employee id 
    @GetMapping("/employee/{employee_id}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployee(@PathVariable("employee_id") Long employeeId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }
    //update the status of leave request 
@PutMapping("/{id}")
public ResponseEntity<LeaveRequest> updateLeaveRequestStatus(@PathVariable Long id, @RequestBody String status) {
    Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);
    if (leaveRequest.isPresent()) {
        LeaveRequest updatedLeaveRequest = leaveRequest.get();

        try {
            LeaveStatus leaveStatus = LeaveStatus.valueOf(status); // Convert String to Enum ✅
            updatedLeaveRequest.setStatus(leaveStatus);
            leaveRequestRepository.save(updatedLeaveRequest);
            return ResponseEntity.ok(updatedLeaveRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Invalid status string
        }
    }
    return ResponseEntity.notFound().build();
}

//delte the leave request 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeaveRequest(@PathVariable Long id) {
        if (leaveRequestRepository.existsById(id)) {
            leaveRequestRepository.deleteById(id);
            return ResponseEntity.ok("Leave request deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
   //approaval of leave request
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeaveRequest(@PathVariable Long id) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);

        if (leaveRequest.isPresent()) {
            LeaveRequest updatedLeaveRequest = leaveRequest.get();
            updatedLeaveRequest.setStatus(LeaveStatus.APPROVED);
            leaveRequestRepository.save(updatedLeaveRequest);

            // Mark Employee as Unavailable
            Optional<Employee> employee = employeeRepository.findById(updatedLeaveRequest.getId());
            employee.ifPresent(emp -> {
                emp.setAvailability(false);
                employeeRepository.save(emp);
            });

            return ResponseEntity.ok(updatedLeaveRequest);
        }
        return ResponseEntity.notFound().build();
    }

    //  Reject Leave Request 
    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectLeaveRequest(@PathVariable Long id) {
        Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);

        if (leaveRequest.isPresent()) {
            LeaveRequest updatedLeaveRequest = leaveRequest.get();
            updatedLeaveRequest.setStatus(LeaveStatus.REJECTED);
            leaveRequestRepository.save(updatedLeaveRequest);

            // Ensure Employee Availability remains True
            Optional<Employee> employee = employeeRepository.findById(updatedLeaveRequest.getId());
            employee.ifPresent(emp -> {
                emp.setAvailability(true);
                employeeRepository.save(emp);
            });

            return ResponseEntity.ok(updatedLeaveRequest);
        }
        return ResponseEntity.notFound().build();
    }
}
