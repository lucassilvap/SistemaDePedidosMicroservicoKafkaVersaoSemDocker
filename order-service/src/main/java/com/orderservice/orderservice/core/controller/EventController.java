package com.orderservice.orderservice.core.controller;

import com.orderservice.orderservice.core.document.Event;
import com.orderservice.orderservice.core.dto.EventFilters;
import com.orderservice.orderservice.core.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private EventService eventService;

    @GetMapping
    public Event findByFilters(@RequestBody  EventFilters eventFilters){
      return eventService.findByFilters(eventFilters);
    }

    @GetMapping("/all")
    public List<Event> findALL(){
       return eventService.findAll();
    }

}
