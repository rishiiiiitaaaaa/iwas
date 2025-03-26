package com.example.iwasCapstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iwasCapstone.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
