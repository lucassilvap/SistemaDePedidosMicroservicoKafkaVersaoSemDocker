package com.example.servicodevalidacaodeproduto.core.ultils;

import com.orquestradorservice.orquestradorservice.core.dto.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

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