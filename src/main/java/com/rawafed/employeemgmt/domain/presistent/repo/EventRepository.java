package com.rawafed.employeemgmt.domain.presistent.repo;

import com.rawafed.employeemgmt.domain.presistent.model.EventModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<EventModel, Integer> {
    void deleteByEmployeeEmailAndEventType(String email, String eventType);
}
