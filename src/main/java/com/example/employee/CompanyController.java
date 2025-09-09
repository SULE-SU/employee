package com.example.employee;

import com.example.employee.exception.CompanyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private List<Company> companies = new ArrayList<>();
    private int id = 0;

    public void clearCompanies() {
        companies.clear();
        id = 0;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@RequestBody Company company) {
        int id = this.id++;
        Company newCompany = new Company(id, company.name());
        companies.add(newCompany);
        return newCompany;
    }

    @GetMapping("/{id}")
    public Company getCompanyById(@PathVariable int id) {
        return companies.stream()
                .filter(company -> company.id() == id)
                .findFirst()
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + id));
    }

}
