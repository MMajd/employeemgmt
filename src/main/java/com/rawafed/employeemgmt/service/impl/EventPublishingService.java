package com.rawafed.employeemgmt.service.impl;

import com.rawafed.employeemgmt.domain.event.IEmployeeEvent;
import com.rawafed.employeemgmt.domain.presistent.repo.EventRepository;
import com.rawafed.employeemgmt.service.IEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublishingService implements IEventPublisherService {
    private final ApplicationEventPublisher eventPublisher;
    private final EventRepository eventRepository;

    @Override
    public void publish(IEmployeeEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Scheduled(fixedRate = 300_000)
    public void handleFailures() {
        // will be scheduled for every 5 minutes
        val events = eventRepository.findAll();
        if (ObjectUtils.isEmpty(events)) {
            return;
        }

        events.forEach(model -> {
            eventPublisher.publishEvent(model.getEvent());
        });
    }

}
