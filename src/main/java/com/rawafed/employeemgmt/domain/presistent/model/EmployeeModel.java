package com.rawafed.employeemgmt.domain.presistent.model;

import com.rawafed.employeemgmt.domain.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @NotEmpty
    @Column
    private String firstName;

    @NotEmpty
    @Column
    private String lastName;

    @NotEmpty
    @Column
    private String department;

    @Column
    private BigDecimal salary;

    @Column(columnDefinition = "boolean default false")
    private Boolean validEmail;

    @Column(columnDefinition = "boolean default false")
    private Boolean validDepartment;

    @Column(length = 255)
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    public Boolean getValidEmail() {
        return this.validEmail == null ? Boolean.FALSE : this.validEmail;
    }

    public Boolean getValidDepartment() {
        return this.validDepartment == null ? Boolean.FALSE : this.validDepartment;
    }


    public boolean verfied() {
        return getValidEmail() && getValidDepartment();
    }

    public void mergeDto(Employee dto) {
        if (isNotEmpty(dto.getDepartment())) {
            setDepartment(dto.getDepartment());
        }
        if (isNotEmpty(dto.getSalary())) {
            setSalary(dto.getSalary());
        }
        if (isNotEmpty(dto.getFirstName())) {
            setFirstName(dto.getFirstName());
        }
        if (isNotEmpty(dto.getLastName())) {
            setLastName(dto.getLastName());
        }
    }
}
