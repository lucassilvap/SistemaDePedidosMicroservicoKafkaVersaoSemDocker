package com.orquestradorservice.orquestradorservice.core.consumer;
import com.orquestradorservice.orquestradorservice.core.service.OrchestratorService;
import com.orquestradorservice.orquestradorservice.core.ultil.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestractorConsumer {

    @Autowired
     private  JsonUltil jsonUltil;
    @Autowired
    private OrchestratorService orchestratorService;

     @KafkaListener(
             groupId ="${spring.kafka.consumer.group-id}",
             topics ="${spring.kafka.template.default-topic}"
     )
     public void cosumerStartSagaEvent(String payload){
         try {
             log.info("Receiving event {} from starg-saga topic", payload);
             var event = jsonUltil.toEvent(payload);
             log.info(event.toString());
             orchestratorService.startSaga(event);
         }catch (Exception ex) {
             log.error("###ERROR "+  ex.getMessage());

         }

     }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic2.spring.kafka.template.default-topic}"
    )
    public void consumerOrchestratorEvent(String payload){
         try {
             log.info("Receiving event {} from orchestrator topic", payload);
             var event = jsonUltil.toEvent(payload);
             log.info(event.toString());
             orchestratorService.continueSaga(event);
         }catch (Exception ex) {
             log.error("##ERROR", ex.getMessage());
         }

    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic3.spring.kafka.template.default-topic}"
    )
    public void finishSucess(String payload){
         try{
             log.info("Receiving event {} from finish sucess topic", payload);
             var event = jsonUltil.toEvent(payload);
             log.info(event.toString());
             orchestratorService.finishSagaSucess(event);
         }catch (Exception ex) {
             log.error("###ERROR" + ex.getMessage());
         }
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${topic4.spring.kafka.template.default-topic}"
    )
    public void finishFail(String payload){
        log.info("Receiving event {} from finish fail topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
        orchestratorService.finishSagaFail(event);
    }





}
