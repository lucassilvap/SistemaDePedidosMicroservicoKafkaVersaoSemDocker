package com.example.servicodevalidacaodeproduto.core.consumer;
import com.example.servicodevalidacaodeproduto.core.dto.Event;
import com.example.servicodevalidacaodeproduto.core.service.ProductValidationService;
import com.example.servicodevalidacaodeproduto.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProducftValidationConsumer {

    private final ProductValidationService productValidationService;

    private final JsonUltil jsonUltil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${product-validation-success.spring.kafka.template.default-topic}"
    )
    public void consumerSucessEvent(String payload){
        log.info("Receiving event {} from product-validation-sucess topic", payload);
        var event  = jsonUltil.toEvent(payload);
        productValidationService.validateExistingProducts(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${product-validation-fail.spring.kafka.template.default-topic}"
    )
    public void consumerFailEvent(String payload){
        log.info("Receiving rollback event {} from product-validation-fail topic", payload);
        var event = jsonUltil.toEvent(payload);
        productValidationService.rollbackEvent(event);
    }
}
