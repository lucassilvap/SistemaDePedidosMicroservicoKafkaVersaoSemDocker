package com.orderservice.orderservice.core.controller;

import com.orderservice.orderservice.core.document.Event;
import com.orderservice.orderservice.core.dto.EventFilters;
import com.orderservice.orderservice.core.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private EventService eventService;

    @GetMapping
    public Event findByFilters(@RequestBody  EventFilters eventFilters){
              Event event1 = eventService.findByFilters(eventFilters);
              log.info("Event entity {} " , event1.toString());
        log.info("Event entity {} " , event1.getEventHistory());
        return  event1;
    }

    @GetMapping("/all")
    public List<Event> findALL(){
       return eventService.findAll();
    }

}
