package com.orderservice.orderservice.core.ultils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.orderservice.core.entity.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonUltil {

private ObjectMapper objectMapper;

    public String toJson(Object object){
        try {
          return objectMapper.writeValueAsString(object);
        }catch (Exception e){
            return "";
        }
    }

    public Event toEvent(String json){
         try {
            return objectMapper.readValue(json, Event.class);
         } catch (Exception e){
             return null;
         }
    }
}
