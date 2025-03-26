// package com.example.iwasCapstone.controller;
// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.iwasCapstone.dto.EmployeeDTO;
// import com.example.iwasCapstone.service.EmployeeService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/employees")
// @RequiredArgsConstructor
// public class EmployeeController {

//     private final EmployeeService employeeService;

//     @PostMapping
//     public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
//         return ResponseEntity.ok(employeeService.addEmployee(employeeDTO));
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
//         return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDTO));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
//         employeeService.deleteEmployee(id);
//         return ResponseEntity.ok("Employee deleted successfully");
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
//         return ResponseEntity.ok(employeeService.getEmployeeById(id));
//     }

//     @GetMapping
//     public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
//         return ResponseEntity.ok(employeeService.getAllEmployees());
//     }
// }

package com.example.iwasCapstone.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Employee;
import com.example.iwasCapstone.repository.EmployeeRepository;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee)
    {
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Employee> getallEmployees()
    {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Employee> getEmployeeById(@PathVariable Long id)
    {
        return employeeRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEmoloyee(@PathVariable Long id)
    {
        employeeRepository.deleteById(id);
}
}