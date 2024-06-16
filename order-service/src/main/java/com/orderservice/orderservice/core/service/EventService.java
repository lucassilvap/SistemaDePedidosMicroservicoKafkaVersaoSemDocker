package com.orderservice.orderservice.core.service;

import com.orderservice.orderservice.config.exception.ValidationException;
import com.orderservice.orderservice.core.document.Event;
import com.orderservice.orderservice.core.dto.EventFilters;
import com.orderservice.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;

    public void notifyEnding(Event event){
           event.setOrderId(event.getOrderId());
           event.setCreatedAt(LocalDateTime.now());
           save(event);
           log.info("Order {} with saga notified! Transaciton id: {}", event.getOrderId(), event.getTransactionId());

    }

    public List<Event> findAll(){
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilters(EventFilters eventFilters){
          validateEmptyFilters(eventFilters);
          if (!isEmpty(eventFilters.getOrderId())) {
              return findByOrderId(eventFilters.getOrderId());
          } else {
              return findByTransactionId(eventFilters.getTransactionId());
          }
    }

    private void validateEmptyFilters(EventFilters eventFilters) {
        if (isEmpty(eventFilters.getOrderId()) && isEmpty(eventFilters.getTransactionId())) {
            throw new ValidationException("Order id or Transaction id must be informed.");
        }
    }

    private Event findByOrderId(String orderId){
        return eventRepository
                .findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ValidationException("Event not found by orderId"));
    }

    private Event findByTransactionId(String transactionId){
        return eventRepository
                .findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new ValidationException("Event not found by transactionId"));
    }


    public void save (Event event){
        eventRepository.save(event);
    }






}
