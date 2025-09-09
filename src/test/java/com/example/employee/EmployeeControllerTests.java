package com.example.employee;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    EmployeeController employeeController;

    @Test
    void should_return_created_employee_when_post() throws Exception {
        String requestBody = """
                {
                    "name": "John Smith",
                    "age": 32,
                    "gender": "Male",
                    "salary": 5000.0
                }
                """;
        MockHttpServletRequestBuilder request = post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.age").value(32))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.salary").value(5000.0));
    }

    @Test
    void should_return_employee_when_get_employee_with_id_exist() throws Exception {

        Employee employee = new Employee(null,"Mike",23,"Male",6000.0);
        Employee expected = employeeController.createEmployee(employee);
        MockHttpServletRequestBuilder request = get("/employees/"+ expected.id())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.name").value("Mike"))
                .andExpect(jsonPath("$.age").value(23))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.salary").value(6000.0));
    }

    @Test
    void should_return_males_employee_when_get_employees_by_gender() throws Exception {
        Employee expected = employeeController.createEmployee(new Employee(null,"Mike",23,"Male",6000.0));
        employeeController.createEmployee(new Employee(null,"lily",23,"Female",6000.0));
        MockHttpServletRequestBuilder request = get("/employees?gender=male")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(expected.id()))
                .andExpect(jsonPath("$[0].name").value(expected.name()))
                .andExpect(jsonPath("$[0].age").value(expected.age()))
                .andExpect(jsonPath("$[0].gender").value(expected.gender()))
                .andExpect(jsonPath("$[0].salary").value(expected.salary()));

    }

}
