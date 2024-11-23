package com.rawafed.employeemgmt.domain.presistent.repo;

import com.rawafed.employeemgmt.domain.presistent.model.EmployeeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeModel, Long>, PagingAndSortingRepository<EmployeeModel, Long> {
    Optional<EmployeeModel> findByEmail(String email);

    void deleteByEmail(String email);
}
