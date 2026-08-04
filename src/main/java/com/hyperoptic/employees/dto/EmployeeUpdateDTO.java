package com.hyperoptic.employees.dto;

import com.hyperoptic.employees.repository.entities.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EmployeeUpdateDTO(
        @NotBlank(message = "Employee name is required")
        @Size(max = 100, message = "Employee name must be at most 100 characters")
        String name,

        @NotNull(message = "Employee Team is required")
        Employee.Team team,

        Long teamLeadId
) { }
