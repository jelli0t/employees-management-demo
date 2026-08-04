package com.hyperoptic.employees.controller;

import com.hyperoptic.employees.dto.EmployeeCreateDTO;
import com.hyperoptic.employees.dto.EmployeeDTO;
import com.hyperoptic.employees.dto.EmployeeUpdateDTO;
import com.hyperoptic.employees.repository.entities.Employee;
import com.hyperoptic.employees.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@Tag(
        name = "Employee Management API",
        description = "API Service used for CRUD operation on Employees."
)
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Creates New Employee")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "OK. Return newly created Employee.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. Employee creation parameters not valid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @ResponseStatus(CREATED)
    public EmployeeDTO create(@Valid @RequestBody EmployeeCreateDTO request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "Search Employees by parameters (optional)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Return Multiple Employee DTOs or Empty.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. Employee Not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @ResponseStatus(OK)
    public List<EmployeeDTO> findAll(
            @RequestParam(required = false)
            @Size(max = 100, message = "Employee name must be at most 100 characters")
            String name,
            @RequestParam(required = false) Employee.Team team,
            @RequestParam(required = false)
            @Size(max = 100, message = "Team Lead's name must be at most 100 characters")
            String teamLead
    ) {
        return service.getAll(name, team, teamLead);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Search Employees by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EmployeeDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bad request. Employee Not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @ResponseStatus(OK)
    public EmployeeDTO findById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Updated successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bad request. Employee Not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @Operation(summary = "Patches Employee's data for provided ID.")
    @ResponseStatus(NO_CONTENT)
    public void update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO request
    ) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bad request. Employee Not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @Operation(summary = "Deletes Employee by provided ID.")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
