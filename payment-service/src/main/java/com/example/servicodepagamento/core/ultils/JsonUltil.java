package com.example.servicodepagamento.core.ultils;

import com.example.servicodepagamento.core.dto.Event;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class JsonUltil {

    @Autowired
    private ObjectMapper objectMapper;


    public String toJson(Object object){
        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(object);
        }catch (Exception e){
            return "";
        }
    }

    public Event toEvent(String json){
        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(json,Event.class);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
