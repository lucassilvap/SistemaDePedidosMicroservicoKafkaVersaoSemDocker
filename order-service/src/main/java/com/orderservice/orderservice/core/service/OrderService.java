package com.orderservice.orderservice.core.service;

import com.orderservice.orderservice.core.document.Event;
import com.orderservice.orderservice.core.document.Order;
import com.orderservice.orderservice.core.dto.OrderRequest;
import com.orderservice.orderservice.core.producer.SagaProducer;
import com.orderservice.orderservice.core.repository.OrderRepository;
import com.orderservice.orderservice.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private static final String TRANSACTION_ID_PARTERN = "%s_%s";

    private final OrderRepository orderRepository;
    private final JsonUltil jsonUltil;
    private final SagaProducer sagaProducer;
    private final ProducerFactory producerFactory;
    private EventService eventService;


     public Order createOrder(OrderRequest orderRequest){
       var order  = Order
               .builder()
               .products(orderRequest.getOrderProduct())
               .localDateTime(LocalDateTime.now())
               .transactionId(
                       String.format(TRANSACTION_ID_PARTERN, Instant.now().toEpochMilli()
                       , UUID.randomUUID())
               )
               .build();
       orderRepository.save(order);
       sagaProducer.sendEvent(jsonUltil.toJson(createPayload(order)));
       return order;

     }

     private Event createPayload(Order order){
            var event = Event
                    .builder()
                    .orderId(order.getId())
                    .transactionId(order.getTransactionId())
                    .payload(order)
                    .createdAt(LocalDateTime.now())
                    .build();
            eventService.save(event);
            return event;
     }

}
