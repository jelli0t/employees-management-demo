package com.hyperoptic.employees.unit;

import com.hyperoptic.employees.dto.EmployeeCreateDTO;
import com.hyperoptic.employees.dto.EmployeeDTO;
import com.hyperoptic.employees.dto.EmployeeUpdateDTO;
import com.hyperoptic.employees.exception.EmployeeNotFoundException;
import com.hyperoptic.employees.mapper.EmployeeMapper;
import com.hyperoptic.employees.repository.EmployeeRepository;
import com.hyperoptic.employees.repository.entities.Employee;
import com.hyperoptic.employees.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeService service;

    private Employee employee;
    private Employee teamLead;

    @BeforeEach
    void setUp() {
        teamLead = new Employee("Mirko", Employee.Team.DEVELOPMENT);
        teamLead.setId(1L);

        employee = new Employee("John Smith", Employee.Team.DEVELOPMENT);
        employee.setId(101L);
        employee.setTeamLead(teamLead);
    }

    @Test
    void should_ReturnOK_When_CreateEmployee() {
        var request = EmployeeCreateDTO.builder()
                .name("John Smith")
                .team(Employee.Team.DEVELOPMENT)
                .teamLeadId(1L)
                .build();

        // Mock find TeamLead
        doReturn(Optional.of(teamLead))
                .when(repository)
                .findById(eq(1L));

        doReturn(employee)
                .when(repository)
                .save(any(Employee.class));

        doReturn(employee)
                .when(mapper)
                .map(eq(request), eq(teamLead));

        doReturn(
                EmployeeDTO.builder()
                        .personalId(101L)
                        .name("John Smith")
                        .teamLead("Mirko")
                        .team(Employee.Team.DEVELOPMENT)
                        .build()
        )
                .when(mapper)
                .map(eq(employee));

        EmployeeDTO response = service.create(request);

        assertNotNull(response);
        assert response.personalId().equals(101L);
        assert response.name().equals(request.name());
        assert response.team().equals(request.team());
        assert response.teamLead().equals("Mirko");

        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldReturn_When_getEmployeeById() {
        when(repository.findById(101L)).thenReturn(Optional.of(employee));
        doReturn(
                EmployeeDTO.builder()
                        .personalId(101L)
                        .name("John Smith")
                        .teamLead("Mirko")
                        .team(Employee.Team.DEVELOPMENT)
                        .build()
        )
                .when(mapper)
                .map(eq(employee));

        EmployeeDTO response = service.getById(101L);

        assertNotNull(response);
        assert response.personalId().equals(101L);
        assert response.name().equals("John Smith");
        assert response.team().equals(Employee.Team.DEVELOPMENT);
        assert response.teamLead().equals("Mirko");
    }

    @Test
    void should_ThrowsNotFound_When_getById_With_NonValidId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: 99");
    }

    @Test
    void should_ReturnOK_When_getAllEmployees() {
        doReturn(
                EmployeeDTO.builder()
                        .personalId(101L)
                        .name("John Smith")
                        .teamLead("Mirko")
                        .team(Employee.Team.DEVELOPMENT)
                        .build()
        )
                .when(mapper)
                .map(eq(employee));

        when(repository.findAll(
                eq(null),
                any(Employee.Team.class),
                any(String.class)
        ))
                .thenReturn(List.of(employee));

        List<EmployeeDTO> responses = service.getAll(null, Employee.Team.DEVELOPMENT, "Mirko");

        assert responses.size() == 1;
        assert responses.get(0).personalId().equals(101L);
    }

    @Test
    void should_updateEmployee_When_ValidPropsRequested() {
        var request = EmployeeUpdateDTO.builder()
                .name("Updated Name")
                .team(Employee.Team.HR)
                .build();

        var updated = new Employee("Updated Name", Employee.Team.HR);
        updated.setId(101L);

        when(repository.findById(101L)).thenReturn(Optional.of(employee));

        doReturn(updated)
                .when(mapper)
                .update(eq(employee), eq(request));

        service.update(101L, request);

        verify(repository).save(argThat(emp ->
                emp.getName().equals("Updated Name")
                        && emp.getTeam().equals(Employee.Team.HR)
                        && emp.getId().equals(101L)
        ));
    }

    @Test
    void should_DeleteEmployee_When_ProperIdRequested() {
        when(repository.findById(101L))
                .thenReturn(Optional.of(employee));

        service.delete(101L);
        verify(repository, times(1)).delete(employee);
    }

    @Test
    void should_ThrowsNotFound_When_TyrDeleteNonValidEmployee() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: 99");
    }
}
