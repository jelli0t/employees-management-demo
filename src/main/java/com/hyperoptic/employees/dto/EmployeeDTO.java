package com.hyperoptic.employees.dto;

import com.hyperoptic.employees.repository.entities.Employee;
import lombok.Builder;

@Builder
public record EmployeeDTO(
        Long personalId,
        String name,
        Employee.Team team,
        String teamLead
) { }
