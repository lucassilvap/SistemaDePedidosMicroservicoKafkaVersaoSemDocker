package com.orquestradorservice.orquestradorservice.core.saga;

import com.orquestradorservice.orquestradorservice.config.exception.ValidationException;
import com.orquestradorservice.orquestradorservice.core.dto.Event;
import com.orquestradorservice.orquestradorservice.core.enums.Etopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.orquestradorservice.orquestradorservice.core.saga.Sagahandler.*;
import static java.lang.String.format;

@Component
@Slf4j
@AllArgsConstructor
public class SagaExecutionController {

    private final String SAGA_LOG_ID ="Order id: %s, | Transaction ID %s |  Event id %s";

    public Etopics getNextTopic(Event event) {
        if (event.getSource().describeConstable().isEmpty() || event.getStatus().describeConstable().isEmpty()) {
            throw new ValidationException("Source and status must be informed");
        }
        var topic = findyTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private Etopics findyTopicBySourceAndStatus(Event event) {
        for (Object[] row : SAGA_HANDLER) {
            if (isEventSourceAndStatusValid(event, row)) {
                return (Etopics) row[TOPIC_INDEX];
            }
        }
        throw new ValidationException("Topic not found!");
    }

    private boolean isEventSourceAndStatusValid(Event event, Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, Etopics etopics) {
          var sagaId = createSagaId(event);
          var source = event.getSource();
          switch (event.getStatus()) {
              case SUCCESS -> log.info("### Current Saga: {} | SUCESS | NEXT TOPIC {} | {}",
                      source, etopics, sagaId);
              case ROLLBACK_PENDING ->  log.info("### Current Saga: {} | Sending to rollback current service | NEXT TOPIC {} | {}",
                      source, etopics, sagaId);
              case FAIL ->  log.info("### Current Saga: {} | Sending to rollback previous service | NEXT TOPIC {} | {}",
                      source, etopics, sagaId);
          }
    }

    private String createSagaId(Event event) {
        return format(SAGA_LOG_ID,
                event.getOrder().getId(), event.getTransactionId(), event.getId());
    }



}
