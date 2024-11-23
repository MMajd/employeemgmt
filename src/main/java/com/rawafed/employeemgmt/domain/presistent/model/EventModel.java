package com.rawafed.employeemgmt.domain.presistent.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_validations")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(columnDefinition = "boolean default false")
    private String employeeEmail;

    @Column
    private String eventType;

    @Column
    private EventObjectEmbedding event;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

}
