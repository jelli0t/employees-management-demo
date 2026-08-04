package com.hyperoptic.employees.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperoptic.employees.dto.EmployeeCreateDTO;
import com.hyperoptic.employees.dto.EmployeeUpdateDTO;
import com.hyperoptic.employees.repository.entities.Employee;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_ReturnCreated_Whe_CreateNewEmployeeRequested() throws Exception {
        var request = EmployeeCreateDTO.builder()
                .name("John Smith")
                .team(Employee.Team.DEVELOPMENT)
                .teamLeadId(123456L)
                .build();

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.personalId", notNullValue()))
                .andExpect(jsonPath("$.name", is("John Smith")))
                .andExpect(jsonPath("$.team", is(Employee.Team.DEVELOPMENT.name())))
                .andExpect(jsonPath("$.teamLead", is("Mirko")));
    }

    @Test
    void should_ReturnBadRequest_When_CreateEmployee_Without_NameProperty() throws Exception {
        var request = EmployeeCreateDTO.builder()
                .name(null)
                .team(Employee.Team.DEVELOPMENT)
                .teamLeadId(123456L)
                .build();

        mockMvc.perform(
                    post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.errors.name", is("Employee name is required")));
    }

    @Test
    void should_ReturnBadRequest_When_CreateEmployee_With_OversizedName() throws Exception {
        var request = EmployeeCreateDTO.builder()
                .name("""
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
                        Ut gravida tortor quis libero laoreet commodo. Vestibulum in augue in mi rhoncus imperdiet. 
                        Fusce rhoncus ante at urna maximus, nec elementum erat lacinia. Maecenas sit amet lacus tempor fusce.
                        """)
                .team(Employee.Team.DEVELOPMENT)
                .teamLeadId(123456L)
                .build();

        mockMvc.perform(
                        post("/api/v1/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.errors.name", is("Employee name must be at most 100 characters")));
    }

    @Test
    void should_ReturnOK_And_GetEmployeeById() throws Exception {
        mockMvc.perform(
                    get("/api/v1/employees/{id}", 987654L)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalId", is(987654)))
                .andExpect(jsonPath("$.name", is("Predrag")))
                .andExpect(jsonPath("$.team", is(Employee.Team.DEVELOPMENT.name())))
                .andExpect(jsonPath("$.teamLead", is("Mirko")));
    }

    @Test
    void should_ReturnNotFound_When_EmployeeDoesNotExist() throws Exception {
        mockMvc.perform(
                        get("/api/v1/employees/{id}", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Employee Not Found")))
                .andExpect(jsonPath("$.detail", is("Employee not found with ID: 99")));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'Mirko', 'DEVELOPMENT', null, 1",
            "null, 'DEVELOPMENT', 'Mirko', 4",
            "'Predrag', null, 'Mirko', 1"
            },
            nullValues = {"null"}
    )
    void should_GetAllEmployees_And_MatchResultSize_When_ValidParamsRequested(
            String name,
            Employee.Team team,
            String teamLead,
            int size
    ) throws Exception {
        mockMvc.perform(
                        get("/api/v1/employees")
                                .queryParam("name", name)
                                .queryParam("team", Optional.ofNullable(team).map(Employee.Team::name).orElse(null))
                                .queryParam("teamLead", teamLead)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)));
    }

    @Test
    void should_UpdateEmployee_And_MatchGetWithRequestedDto() throws Exception {
        var request = EmployeeUpdateDTO.builder()
                .name("Updated Name")
                .team(Employee.Team.HR)
                .teamLeadId(987654L)
                .build();

        // When
        mockMvc.perform(
                patch("/api/v1/employees/{id}", 654321L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNoContent());
        // Then
        mockMvc.perform(
                        get("/api/v1/employees/{id}", 654321L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalId", is(654321)))
                .andExpect(jsonPath("$.name", is(request.name())))
                .andExpect(jsonPath("$.team", is(request.team().name())))
                .andExpect(jsonPath("$.teamLead", is("Predrag")));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void should_DeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/{id}", 654321))
            .andExpect(status().isNoContent());
    }

    @Test
    void should_ReturnNotFound_When_DeletingNonExistentEmployee() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Employee Not Found")));
    }
}
