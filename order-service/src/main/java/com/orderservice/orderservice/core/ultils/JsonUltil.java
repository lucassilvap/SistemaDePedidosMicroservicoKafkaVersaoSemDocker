package com.orderservice.orderservice.core.ultils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.orderservice.core.document.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class JsonUltil {

@Autowired
private ObjectMapper objectMapper;

    public String toJson(Object object){
        try {
            log.info(object.toString());
          return objectMapper.writeValueAsString(object);
        }catch (Exception e){
            log.error("###ERROR", e.getCause());
            return "";
        }
    }

    public Event toEvent(String json){
         try {
             log.info(json);
            return objectMapper.readValue(json, Event.class);
         } catch (Exception e){
             log.error("###ERROR", e.getCause());
             return null;
         }
    }
}
