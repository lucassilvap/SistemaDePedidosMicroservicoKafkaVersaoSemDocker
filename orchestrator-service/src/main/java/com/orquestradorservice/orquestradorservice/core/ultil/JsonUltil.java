package com.orquestradorservice.orquestradorservice.core.ultil;

import com.orquestradorservice.orquestradorservice.core.dto.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class JsonUltil {

    @Autowired
    private ObjectMapper objectMapper;

    public String toJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        }catch (Exception e){
            log.error("## ERROR {}", e.getMessage());
            return "";
        }
    }

    public Event toEvent(String json){
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e){
            log.error("## ERROR {}", e.getMessage());
            return null;
        }
    }

}
