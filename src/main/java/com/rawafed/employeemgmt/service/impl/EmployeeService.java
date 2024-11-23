package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.Employee;
import com.rawafed.employeemgmt.domain.event.EmployeeRegistrationEvent;
import com.rawafed.employeemgmt.domain.exception.ResourceNotFoundException;
import com.rawafed.employeemgmt.domain.exception.VerificationRequiredException;
import com.rawafed.employeemgmt.domain.presistent.model.EmployeeModel;
import com.rawafed.employeemgmt.domain.presistent.repo.EmployeeRepository;
import com.rawafed.employeemgmt.service.IEmployeeService;
import com.rawafed.employeemgmt.service.IEventPublisherService;
import com.rawafed.employeemgmt.utils.PaginationUtils;
import com.rawafed.employeemgmt.utils.mapper.EmployeeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;
    private final IEventPublisherService eventPublisherService;

    @Transactional
    @Override
    public void create(Employee dto) {
        log.trace("Saving employee {} to database", dto.getEmail());
        repository.save(mapper.dtoToModel(dto));

        log.trace("Publishing employee registration event");
        eventPublisherService.publish(
                EmployeeRegistrationEvent
                        .builder()
                        .email(dto.getEmail())
                        .department(dto.getDepartment())
                        .build()
        );
    }

    @Transactional
    @Override
    public void internalUpdate(Employee dto) {
        log.debug("Received internal update request with data: {}", dto);
        EmployeeModel employee = findByEmailOrElseThrow(dto.getEmail());
        employee.mergeDto(dto);
        log.debug("Employee values {} in database", employee);
        repository.save(employee);
    }

    @Transactional
    @Override
    public void update(Employee dto) {
        EmployeeModel employee = findByEmailOrElseThrow(dto.getEmail());
        canRetrieveOrModify(employee);
        employee.mergeDto(dto);
        log.debug("New employee values {} in database", employee);
        repository.save(employee);
    }

    @Transactional
    @Override
    public void delete(String email) {
        repository.deleteByEmail(email);
    }

    @Transactional
    @Override
    public Employee find(String email) {
        log.debug("Find operation triggered for email {}", email);
        EmployeeModel employee = findByEmailOrElseThrow(email);
        log.debug("Retrieved data for email: {}, with ID: {}", email, employee.getId());
        return mapper.modelToDto(employee);
    }

    @Transactional
    @Override
    public List<Employee> list(Integer pageNo) {
        Pageable pageRequest = PaginationUtils.createPageRequest(pageNo);
        log.debug("Page request data: offset={}, records_size={}", pageRequest.getOffset(), pageRequest.getPageSize());

        Page<EmployeeModel> page = repository.findAll(pageRequest);
        log.debug("Page data: {}", page.getContent());

        return page.getContent()
                .stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
    }

    private EmployeeModel findByEmailOrElseThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> {
                    log.debug("Employee with email: {} not found in the database", email);
                    return new ResourceNotFoundException();
                });
    }

    private EmployeeModel findByEmailOrElseNull(String email) {
        return findByEmail(email).orElseGet(() -> null);
    }

    @Transactional
    private Optional<EmployeeModel> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    private void canRetrieveOrModify(EmployeeModel employeeModel) {
        if (!employeeModel.verfied()) {
            throw new VerificationRequiredException("Sorry we cannot update employee information till it's verified please check again later");
        }
    }
}
