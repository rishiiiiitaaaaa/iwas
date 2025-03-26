package com.example.iwasCapstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.iwasCapstone.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
