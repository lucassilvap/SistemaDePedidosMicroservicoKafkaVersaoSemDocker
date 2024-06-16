package com.orquestradorservice.orquestradorservice.core.producer;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SagaOrchestratorProducer {

    @Autowired
   private KafkaTemplate<String, String> kafkaTemplate;

   public void sendEvent(String payload, String topic){

       try {
            log.info("Sending event to topic {} with data {}", topic, payload);
            kafkaTemplate.send(topic, payload);
       }catch (Exception ex){
           log.error("Error trying to send data to topic {} with data {} ", topic, payload );
       }
   }
}
