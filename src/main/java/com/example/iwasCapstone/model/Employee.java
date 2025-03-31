package com.example.iwasCapstone.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // @ElementCollection(fetch = FetchType.LAZY)
@JsonProperty("skills")
    // @CollectionTable(name = "employee_skills", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skills",nullable = false )
    private List<String> skills;
    

    @Column(nullable = false)
    private boolean availability = true;

    @Column(nullable = false)
    private String role;
      @Column(nullable = true) 
      private String password;
      
// Getters and Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public boolean getAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    public String getRole() { return role; } 
    public void setRole(String role) { this.role = role; } 
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
//constructors when  used by admin , employee
    public Employee(String name, String email, String role, List<String> skills) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.skills = skills;
        this.availability = true;
    }
    public Employee(String name, String email, String role, List<String> skills,String password) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.skills = skills;
        this.availability = true;
        this.password=password;
    }
}
