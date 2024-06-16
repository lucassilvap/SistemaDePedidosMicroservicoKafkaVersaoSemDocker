package com.example.servicodeinventario.core.service;

import com.example.servicodeinventario.config.exception.ValidationException;
import com.example.servicodeinventario.core.dto.Event;
import com.example.servicodeinventario.core.dto.History;
import com.example.servicodeinventario.core.dto.Order;
import com.example.servicodeinventario.core.dto.OrderProduct;
import com.example.servicodeinventario.core.entity.Inventory;
import com.example.servicodeinventario.core.entity.OrderInventory;
import com.example.servicodeinventario.core.enums.ESagaStatus;
import com.example.servicodeinventario.core.producer.KafkaProducer;
import com.example.servicodeinventario.core.repository.InventoryRepository;
import com.example.servicodeinventario.core.repository.OrderInventoryRepository;
import com.example.servicodeinventario.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";

    @Autowired
    private JsonUltil jsonUltil;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderInventoryRepository orderInventoryRepository;

    public void updateInventory(Event event) {
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventory(event.getPayload());
            handleSucess(event);
        }catch (Exception e){
            log.error("Error trying to update inventory: ", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUltil.toJson(event));
    }


    private void checkCurrentValidation(Event event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There is another transaction id  or order id for this validation");
        }
    }
    private void createOrderInventory(Event event) {
        event.getPayload()
                .getProducts()
                .forEach(product ->{
                      var inventory = findyInventoryByProductCode(product.getProduct().getCode());
                      var orderInventory = createOrderInventory(event, product, inventory);
                      orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProduct product, Inventory inventory){
            return  OrderInventory
                    .builder()
                    .inventory(inventory)
                    .oldQuantity(inventory.getAvailable())
                    .orderQuantity(product.getQuantity())
                    .newQuantity(inventory.getAvailable() - product.getQuantity())
                    .orderId(event.getPayload().getId())
                    .transactionId(event.getTransactionId())
                    .build();
    }

    private void updateInventory(Order order) {
        order.getProducts()
                .forEach(product ->{
                    var inventory = findyInventoryByProductCode(product.getProduct().getCode());
                    checkInventory(inventory.getAvailable(), product.getQuantity());
                    inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
                    inventoryRepository.save(inventory);
                });
        
    }

    private void checkInventory(int available, int orderQunatity) {
        if (orderQunatity > available) {
            throw new ValidationException("Product is out of stock!");
        }

    }
    private void handleSucess(Event event){
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Inventory update sucess");
    }

    private void addHistory(Event event, String message) {
        var history  = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to upadte event ".concat(message));
    }

    public void rollbackEvent(Event event) {
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        try {
            returnInventoryToPreviusValues(event);
            addHistory(event, "Rollback executed for event");
        }catch (Exception e) {
            addHistory(event, "Rollback not executed for event".concat(e.getMessage()));
        }
    }

    private void returnInventoryToPreviusValues(Event event){
               orderInventoryRepository
                       .findByOrderIdAndTransactionId(event.getPayload().getId(),
                               event.getTransactionId())
                       .forEach(orderInventory ->{
                           var inventory = orderInventory.getInventory();
                           inventory.setAvailable(orderInventory.getOldQuantity());
                           inventoryRepository.save(inventory);
                           log.info("Restored inventory for order {} from {} to {}", event.getPayload().getId(),
                                   orderInventory.getNewQuantity(), inventory.getAvailable());
                       });

    }

    private Inventory findyInventoryByProductCode(String productCode) {
        return inventoryRepository
                .findByProductCode(productCode)
                .orElseThrow(()-> new ValidationException("Inventory not found by informed product code"));
    }


}
