package com.hyperoptic.employees.service;

import com.hyperoptic.employees.dto.EmployeeCreateDTO;
import com.hyperoptic.employees.dto.EmployeeDTO;
import com.hyperoptic.employees.dto.EmployeeUpdateDTO;
import com.hyperoptic.employees.exception.EmployeeNotFoundException;
import com.hyperoptic.employees.mapper.EmployeeMapper;
import com.hyperoptic.employees.repository.EmployeeRepository;
import com.hyperoptic.employees.repository.entities.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Transactional
    public EmployeeDTO create(EmployeeCreateDTO request) {
        var teamLeadOpt = Optional.ofNullable(request.teamLeadId())
                .map(this::findOrThrow);

        var employee = teamLeadOpt
                .map(teamLad -> mapper.map(request, teamLad))
                .orElseGet(() -> mapper.map(request));

        return mapper.map(repository.save(employee));
    }


    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAll(
            String name,
            Employee.Team team,
            String teamLead
    ) {
        return repository.findAll(name, team, teamLead)
                .stream()
                .map(mapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getById(Long id) {
        return mapper.map(
                findOrThrow(id)
        );
    }

    @Transactional
    public void update(Long id, EmployeeUpdateDTO request) {
        var employee = findOrThrow(id);
        var updated = mapper.update(employee, request);

        if (request.teamLeadId() != null) {
            var teamLead = findOrThrow(request.teamLeadId());
            updated.setTeamLead(teamLead);
        }

        repository.save(updated);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findOrThrow(id);

        repository.delete(employee);
        log.info("Employee {} deleted.", id);
    }

    public Employee findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
