package com.rawafed.employeemgmt.operation;

import com.rawafed.employeemgmt.api.EmployeesApi;
import com.rawafed.employeemgmt.api.model.EmployeeCreateReq;
import com.rawafed.employeemgmt.api.model.EmployeeRes;
import com.rawafed.employeemgmt.api.model.EmployeeUpdateReq;
import com.rawafed.employeemgmt.domain.exception.UnprocessableInputException;
import com.rawafed.employeemgmt.service.IEmployeeService;
import com.rawafed.employeemgmt.utils.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmployeeOperation implements EmployeesApi {
    private final IEmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @Override
    public ResponseEntity<Void> v1CreateEmployee(EmployeeCreateReq employeeCreateReq) throws Exception {
        log.info("Received an employee create request with data: {}", employeeCreateReq);
        val dto = employeeMapper.apiCreateToDto(employeeCreateReq);
        employeeService.create(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<EmployeeRes> v1GetEmployee(String email) throws Exception {
        log.info("Received an employee read for email: {}", email);
        val dto = employeeService.find(email);
        return new ResponseEntity<>(employeeMapper.dtoToApi(dto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EmployeeRes>> v1ListEmployees(Integer pageNo) throws Exception {
        log.info("Received employees list request for page: {}", pageNo);
        val dtos = employeeService.list(pageNo);
        return new ResponseEntity<>(employeeMapper.dtoListToApiList(dtos), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> v1UpdateEmployee(String email, EmployeeUpdateReq employeeUpdateReq) throws Exception {
        log.info("Received employee update request for employee: {}, and data: {}", email, employeeUpdateReq);
        EmployeeUpdateReq emptyUpdateReq = new EmployeeUpdateReq();
        if (emptyUpdateReq.equals(employeeUpdateReq)) {
            throw new UnprocessableInputException("Empty EmployeeUpdateReq object, " +
                    "object has to have at least one property set to some value");
        }
        val dto = employeeMapper.apiUpdateToDto(email, employeeUpdateReq);
        employeeService.update(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
