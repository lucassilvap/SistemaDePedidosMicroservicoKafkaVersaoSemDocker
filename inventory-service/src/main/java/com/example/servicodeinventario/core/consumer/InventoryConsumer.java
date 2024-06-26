package com.example.servicodeinventario.core.consumer;
import com.example.servicodeinventario.core.service.InventoryService;
import com.example.servicodeinventario.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    @Autowired
    private JsonUltil jsonUltil;
    @Autowired
    private InventoryService inventoryService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${inventory-success.spring.kafka.template.default-topic}"
    )
    public void consumerSucessEvent(String payload){
        log.info("##CAI AQUI NO CONSUMER SUCCESS");
        log.info("Receiving event {} from   inventory-success topic", payload);
        var event = jsonUltil.toEvent(payload);
        inventoryService.updateInventory(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${inventory-fail.spring.kafka.template.default-topic}"
    )
    public void consumerFailEvent(String payload){
        log.info("Receiving rollback event {} from inventory-fail topic", payload);
        var event = jsonUltil.toEvent(payload);
        inventoryService.rollbackEvent(event);
    }
}
