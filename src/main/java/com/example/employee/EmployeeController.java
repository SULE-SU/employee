package com.example.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private List<Employee> employees = new ArrayList<>();
    private int id = 0;

    public void clearEmployees() {
        employees.clear();
        id = 0;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@RequestBody Employee employee) {
        int id = this.id++;
        Employee newEmployee = new Employee(id, employee.name(), employee.age(), employee.gender(), employee.salary());
        employees.add(newEmployee);
        return newEmployee;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        return employees.stream()
                .filter(employee -> employee.id() == id)
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @GetMapping
    public List<Employee> getEmployeesByGender(@RequestParam(required =false) String gender,
                                               @RequestParam(required =false)  Integer page,
                                               @RequestParam(required =false)  Integer pageSize) {
        if (page != null && pageSize != null) {
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, employees.size());
            return employees.subList(fromIndex, toIndex);
        }
        if (gender == null) {
            return employees;
        }
        return employees.stream()
                .filter(employee -> employee.gender().compareToIgnoreCase(gender)==0)
                .toList();
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable int id, @RequestBody Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            if (employee.id() == id) {
                Employee newEmployee = new Employee(
                        id,
                        updatedEmployee.name(),
                        updatedEmployee.age(),
                        updatedEmployee.gender(),
                        updatedEmployee.salary()
                );
                employees.set(i, newEmployee);
                return newEmployee;
            }
        }
        throw new EmployeeNotFoundException("Employee not found with id: " + id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable int id) {
        int index =0;
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            if (employee.id() == id) {
               index = i;
               break;
            }
        }
        employees.remove(index);
    }


}
