package com.example.employee;


import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        employeeController.clearEmployees();
    }

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

        Employee employee = new Employee(null, "Mike", 23, "Male", 6000.0);
        Employee expected = employeeController.createEmployee(employee);
        MockHttpServletRequestBuilder request = get("/employees/" + expected.id())
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
        Employee expected = employeeController.createEmployee(new Employee(null, "Mike", 23, "Male", 6000.0));
        employeeController.createEmployee(new Employee(null, "lily", 23, "Female", 6000.0));
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

    @Test
    void should_return_employees_when_get_employees_list() throws Exception {
        Employee employee1 = employeeController.createEmployee(new Employee(null, "Mike", 23, "Male", 6000.0));
        Employee employee2 = employeeController.createEmployee(new Employee(null, "Lily", 25, "Female", 7000.0));

        MockHttpServletRequestBuilder request = get("/employees")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(employee1.id()))
                .andExpect(jsonPath("$[0].name").value(employee1.name()))
                .andExpect(jsonPath("$[0].age").value(employee1.age()))
                .andExpect(jsonPath("$[0].gender").value(employee1.gender()))
                .andExpect(jsonPath("$[0].salary").value(employee1.salary()))
                .andExpect(jsonPath("$[1].id").value(employee2.id()))
                .andExpect(jsonPath("$[1].name").value(employee2.name()))
                .andExpect(jsonPath("$[1].age").value(employee2.age()))
                .andExpect(jsonPath("$[1].gender").value(employee2.gender()))
                .andExpect(jsonPath("$[1].salary").value(employee2.salary()));
    }

    @Test
    void should_return_employee_when_update_an_employee_info() throws Exception {
        Employee employee = employeeController.createEmployee(new Employee(null, "Mike", 23, "Male", 6000.0));
        int id = employee.id();

        String updateRequestBody = """
                {
                    "name": "Michael",
                    "age": 24,
                    "gender": "Male",
                    "salary": 6500.0
                }
                """;

        MockHttpServletRequestBuilder request = put("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Michael"))
                .andExpect(jsonPath("$.age").value(24))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.salary").value(6500.0));
    }

    @Test
    void should_return_response_code_when_delete_an_employee() throws Exception {
        Employee employee = employeeController.createEmployee(new Employee(null, "Mike", 23, "Male", 6000.0));
        int id = employee.id();

        MockHttpServletRequestBuilder request = delete("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_page_query_info_when_page_query_with_page_1_and_size_5() throws Exception {
        for (int i = 1; i <= 6; i++) {
            employeeController.createEmployee(new Employee(null, "Employee" + i, 20 + i, "Male", 5000.0 + i));
        }

        MockHttpServletRequestBuilder request = get("/employees" + "?page=1&pageSize=5")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Employee1"))
                .andExpect(jsonPath("$[4].name").value("Employee5"));
    }


}
