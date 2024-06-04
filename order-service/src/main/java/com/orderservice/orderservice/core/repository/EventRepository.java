package com.orderservice.orderservice.core.repository;
import com.orderservice.orderservice.core.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByOrderByCreatedAtDesc();
    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);
    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String trasactionId);
}