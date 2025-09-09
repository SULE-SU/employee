package com.example.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CompanyController companyController;

    @BeforeEach
    void setUp() {
        companyController.clearCompanies();
    }

    @Test
    void should_return_created_company_when_post() throws Exception {
        String requestBody = """
                {
                    "name": "Spring"
                }
                """;
        MockHttpServletRequestBuilder request = post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Spring"));
    }

    @Test
    void should_return_company_when_get_company_with_id_exist() throws Exception {
        Company company = new Company(null, "Spring");
        Company expected = companyController.createCompany(company);

        MockHttpServletRequestBuilder request = get("/companies/" + expected.id())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.name").value("Spring"));
    }

    @Test
    void should_return_companies_when_get_companies_list() throws Exception {
        Company company1 = companyController.createCompany(new Company(null, "Spring"));
        Company company2 = companyController.createCompany(new Company(null, "Google"));

        MockHttpServletRequestBuilder request = get("/companies")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(company1.id()))
                .andExpect(jsonPath("$[0].name").value(company1.name()))
                .andExpect(jsonPath("$[1].id").value(company2.id()))
                .andExpect(jsonPath("$[1].name").value(company2.name()));
    }

    @Test
    void should_return_page_query_info_when_page_query_with_page_1_and_size_5() throws Exception {
        for (int i = 1; i <= 6; i++) {
            companyController.createCompany(new Company(null, "Company" + i));
        }

        MockHttpServletRequestBuilder request = get("/companies?page=1&pageSize=5")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Company1"))
                .andExpect(jsonPath("$[4].name").value("Company5"));
    }

    @Test
    void should_return_company_when_update_a_company_info() throws Exception {
        Company company = companyController.createCompany(new Company(null, "Spring"));
        int id = company.id();

        String updateRequestBody = """
                {
                    "name": "Spring Framework"
                }
                """;

        MockHttpServletRequestBuilder request = put("/companies/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Spring Framework"));
    }

    @Test
    void should_return_response_code_when_delete_a_company() throws Exception {
        Company company = companyController.createCompany(new Company(null, "Spring"));
        int id = company.id();

        MockHttpServletRequestBuilder request = delete("/companies/" + id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }



}
