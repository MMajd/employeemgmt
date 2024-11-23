package com.rawafed.employeemgmt.service;

import com.rawafed.employeemgmt.domain.Employee;

import java.util.List;

public interface IEmployeeService {
    void create(Employee dto);

    void internalUpdate(Employee dto);

    void update(Employee dto);

    void delete(String email);

    Employee find(String email);

    List<Employee> list(Integer pageNo);
}
