package com.example.servicodeinventario.core.service;

import com.example.servicodeinventario.config.exception.ValidationException;
import com.example.servicodeinventario.core.dto.Event;
import com.example.servicodeinventario.core.dto.OrderProduct;
import com.example.servicodeinventario.core.entity.Inventory;
import com.example.servicodeinventario.core.entity.OrderInventory;
import com.example.servicodeinventario.core.producer.KafkaProducer;
import com.example.servicodeinventario.core.repository.InventoryRepository;
import com.example.servicodeinventario.core.repository.OrderInventoryRepository;
import com.example.servicodeinventario.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        }catch (Exception e){
            log.error("Error trying to update inventory: ", e);
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


    private Inventory findyInventoryByProductCode(String productCode) {
        return inventoryRepository
                .findByProductCode(productCode)
                .orElseThrow(()-> new ValidationException("Inventory not found by informed product code"));
    }


}
