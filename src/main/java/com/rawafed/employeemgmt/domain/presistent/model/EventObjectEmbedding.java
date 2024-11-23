package com.rawafed.employeemgmt.domain.presistent.model;

import com.rawafed.employeemgmt.domain.event.EmployeeBaseEvent;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Embeddable
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EventObjectEmbedding extends EmployeeBaseEvent {
}
