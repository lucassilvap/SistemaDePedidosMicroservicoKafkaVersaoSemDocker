package com.orquestradorservice.orquestradorservice.core.service;

import com.orquestradorservice.orquestradorservice.core.dto.Event;
import com.orquestradorservice.orquestradorservice.core.dto.History;
import com.orquestradorservice.orquestradorservice.core.enums.EEventSource;
import com.orquestradorservice.orquestradorservice.core.enums.ESagaStatus;
import com.orquestradorservice.orquestradorservice.core.enums.Etopics;
import com.orquestradorservice.orquestradorservice.core.producer.SagaOrchestratorProducer;
import com.orquestradorservice.orquestradorservice.core.saga.SagaExecutionController;
import com.orquestradorservice.orquestradorservice.core.ultil.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.orquestradorservice.orquestradorservice.core.enums.Etopics.NOTIFY_ENDING;

@AllArgsConstructor
@Service
@Slf4j
public class OrchestratorService {

    @Autowired
    private JsonUltil jsonUltil;
    @Autowired
    private SagaOrchestratorProducer sagaOrchestratorProducer;
    @Autowired
    private SagaExecutionController sagaExecutionController;
    @Autowired
    private NewTopic notifyEndingSucessTopic;

    public void startSaga (Event event){
            event.setSource(EEventSource.ORCHESTRATOR);
            event.setStatus(ESagaStatus.SUCCESS);
            var topic = getTopic(event);
            log.info("SAGA STARTED!");
            addHistory(event, "Saga started!");
            sagaOrchestratorProducer.sendEvent(jsonUltil.toJson(event), topic.getTopic());
    }
    public void finishSagaSucess(Event event){
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.SUCCESS);
        log.info("SAGA Finished SUCESS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished sucess!");
        notifyFinishedSaga(event);
    }

    public void finishSagaFail(Event event){
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.FAIL);
        log.info("SAGA Finished WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished sucess!");
        notifyFinishedSaga(event);
    }

    public void continueSaga(Event event){
        var topic = getTopic(event);
        log.info("SAGA CONTIUNE FOR EVENT {}", event.getId());
        sagaOrchestratorProducer.sendEvent(jsonUltil.toJson(event), topic.getTopic());
    }

    private Etopics getTopic(Event event) {
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void notifyFinishedSaga(Event event) {
        sagaOrchestratorProducer.sendEvent(jsonUltil.toJson(event), NOTIFY_ENDING.getTopic());



    }
}



