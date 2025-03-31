package com.example.iwasCapstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iwasCapstone.model.Assignment;

@Repository
public interface AllocationRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByProjectId(Long projectId);
    List<Assignment> findByEmployeeId(Long employeeId);
}
