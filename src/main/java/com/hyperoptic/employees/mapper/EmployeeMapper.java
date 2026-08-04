package com.hyperoptic.employees.mapper;

import com.hyperoptic.employees.dto.EmployeeCreateDTO;
import com.hyperoptic.employees.dto.EmployeeDTO;
import com.hyperoptic.employees.dto.EmployeeUpdateDTO;
import com.hyperoptic.employees.repository.entities.Employee;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teamLead", ignore = true)
    Employee map(EmployeeCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "createDTO.name")
    @Mapping(target = "team", source = "createDTO.team")
    @Mapping(target = "teamLead", source = "teamLead")
    Employee map(EmployeeCreateDTO createDTO, Employee teamLead);

    @Mapping(target = "personalId", source = "id")
    @Mapping(target = "teamLead", source = "teamLead.name")
    EmployeeDTO map(Employee entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teamLead", ignore = true)
    @Mapping(target = "name", source = "updateDTO.name")
    @Mapping(target = "team", source = "updateDTO.team")
    Employee update(@MappingTarget Employee entity, EmployeeUpdateDTO updateDTO);
}
