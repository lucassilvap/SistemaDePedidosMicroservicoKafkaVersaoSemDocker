package com.orderservice.orderservice.core.ultils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.orderservice.core.document.Event;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonUltil {

@Autowired
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
