package com.example.servicodeinventario.core.repository;

import com.example.servicodeinventario.core.entity.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Long> {

    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    List<OrderInventory> findByOrderIdAndTransactionId(String order, String transactionId);

}
