package com.orderservice.orderservice.core.consumer;


import com.orderservice.orderservice.core.service.EventService;
import com.orderservice.orderservice.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

     private final JsonUltil jsonUltil;
     private final EventService eventService;

     @KafkaListener(
             groupId = "${spring.kafka.consumer.group-id}",
             topics = "${topic2.spring.kafka.template.default-topic}"
     )
     public void consumerNotifyEndingEvent(String payload){
           log.info("Receiving ending notification event {} from notify_ending topic", payload);
           var event = jsonUltil.toEvent(payload);
           log.info(event.toString());
           eventService.notifyEnding(event);
     }


}
