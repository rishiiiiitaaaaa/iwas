package com.example.iwasCapstone.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iwasCapstone.model.LeaveRequest;
import com.example.iwasCapstone.model.LeaveStatus;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Find leave requests by employee ID
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    // Find approved leave requests for an employee
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    // Find leave requests within a specific date range
    List<LeaveRequest> findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId, LeaveStatus status, LocalDate today1, LocalDate today2);
}
