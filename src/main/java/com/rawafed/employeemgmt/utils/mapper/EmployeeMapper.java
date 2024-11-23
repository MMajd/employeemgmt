package com.rawafed.employeemgmt.utils.mapper;

import com.rawafed.employeemgmt.api.model.EmployeeCreateReq;
import com.rawafed.employeemgmt.api.model.EmployeeRes;
import com.rawafed.employeemgmt.api.model.EmployeeUpdateReq;
import com.rawafed.employeemgmt.domain.Employee;
import com.rawafed.employeemgmt.domain.presistent.model.EmployeeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmployeeMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "validEmail", ignore = true),
            @Mapping(target = "validDepartment", ignore = true),
            @Mapping(target = "notes", ignore = true)
    })
    Employee apiCreateToDto(EmployeeCreateReq employeeCreateReq);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "validEmail", ignore = true),
            @Mapping(target = "validDepartment", ignore = true),
            @Mapping(target = "notes", ignore = true)
    })
    Employee apiUpdateToDto(String email, EmployeeUpdateReq employeeUpdateReq);

    @Mappings({
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "modifiedAt", ignore = true),
    })
    EmployeeModel dtoToModel(Employee employee);

    Employee modelToDto(EmployeeModel employee);

    EmployeeRes dtoToApi(Employee dto);

    List<EmployeeRes> dtoListToApiList(List<Employee> dtoList);
}
