package com.example.iwasCapstone.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
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
    //function created for fixing availability of employee with leave status
    public boolean checkEmployeeLeaveStatus(Long employeeId) {
        LocalDate today = LocalDate.now();
    
        // Find active leave requests that overlap with today
        List<LeaveRequest> approvedLeaves = leaveRequestRepository
            .findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, today, today);
    
        return !approvedLeaves.isEmpty();  // If list is not empty, employee is on leave
    }
    

    //get all leave request 
    @GetMapping
public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
    List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
    return ResponseEntity.ok(leaveRequests);
}

//create a leave request 
    @PostMapping
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        System.out.println("Received Leave Request: " + leaveRequest);
        
        if (leaveRequest.getEmployee() == null || leaveRequest.getEmployee().getId() == null) {
            return ResponseEntity.badRequest().body("Employee ID is missing.");
        }
    
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
public ResponseEntity<?> updateLeaveRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
    // Debugging: Log received request
    System.out.println("Received PUT request for LeaveRequest ID: " + id);
    System.out.println("Received Body: " + requestBody);

    // Validate Request Body
    if (requestBody == null || !requestBody.containsKey("status")) {
        return ResponseEntity.badRequest().body("Missing 'status' in request body");
    }

    String status = requestBody.get("status").toUpperCase(); // Convert to uppercase

    Optional<LeaveRequest> leaveRequest = leaveRequestRepository.findById(id);
    if (leaveRequest.isPresent()) {
        LeaveRequest updatedLeaveRequest = leaveRequest.get();

        try {
            LeaveStatus leaveStatus = LeaveStatus.valueOf(status); // Convert String to Enum to match the storage with leavestatus entity
            updatedLeaveRequest.setStatus(leaveStatus);
            leaveRequestRepository.save(updatedLeaveRequest);

            return ResponseEntity.ok(updatedLeaveRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value. Must be 'APPROVED' or 'REJECTED'.");
        }
    }
    return ResponseEntity.notFound().build();
}


  //leave approve api 
@RequestMapping("/approve/{id}")
public ResponseEntity<String> approveLeave(@PathVariable Long id) {
    LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
    if (leaveRequest == null) {
        return ResponseEntity.notFound().build();
    }

    leaveRequest.setStatus(LeaveStatus.APPROVED);
    leaveRequestRepository.save(leaveRequest);

    // Update employee availability immediately
    Employee employee = leaveRequest.getEmployee();
    boolean isOnLeave = checkEmployeeLeaveStatus(employee.getId());
    employee.setAvailability(!isOnLeave);  // If on leave → false, else true
    employeeRepository.save(employee);

    return ResponseEntity.ok("Leave Approved and Availability Updated");
}
// leave reject api
@RequestMapping("/reject/{id}")
public ResponseEntity<String> rejectLeave(@PathVariable Long id) {
    LeaveRequest leaveRequest = leaveRequestRepository.findById(id).orElse(null);
    if (leaveRequest == null) {
        return ResponseEntity.notFound().build();
    }

    leaveRequest.setStatus(LeaveStatus.REJECTED);
    leaveRequestRepository.save(leaveRequest);

    // Update employee availability (mark as available if no other approved leaves exist)
    Employee employee = leaveRequest.getEmployee();
    boolean isOnLeave = checkEmployeeLeaveStatus(employee.getId());
    employee.setAvailability(!isOnLeave);
    employeeRepository.save(employee);

    return ResponseEntity.ok("Leave Rejected and Employee Marked as Available");
}

}
