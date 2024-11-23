package com.rawafed.employeemgmt.service;


import com.rawafed.employeemgmt.domain.event.IEmployeeEvent;

public interface IEventPublisherService {
    void publish(IEmployeeEvent event);
}
