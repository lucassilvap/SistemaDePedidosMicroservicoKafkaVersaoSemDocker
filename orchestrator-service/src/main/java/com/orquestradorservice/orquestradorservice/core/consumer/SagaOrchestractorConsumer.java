package com.orquestradorservice.orquestradorservice.core.consumer;
import com.orquestradorservice.orquestradorservice.core.ultil.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestractorConsumer {

     private final JsonUltil jsonUltil;

     @KafkaListener(
             groupId = "${spring.kafka.consumer.group-id}",
             topics = "${spring.kafka.template.default-topic}"
     )
     public void consumerStartSagaEvent(String payload){
           log.info("Receiving event {} from starg-saga topic", payload);
           var event = jsonUltil.toEvent(payload);
           log.info(event.toString());
     }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic2.spring.kafka.template.default-topic}"
    )
    public void consumerOrchestratorEvent(String payload){
        log.info("Receiving event {} from orchestrator topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic3.spring.kafka.template.default-topic}"
    )
    public void finishSucess(String payload){
        log.info("Receiving event {} from finish sucess topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic4.spring.kafka.template.default-topic}"
    )
    public void finishFail(String payload){
        log.info("Receiving event {} from finish fail topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
    }





}
