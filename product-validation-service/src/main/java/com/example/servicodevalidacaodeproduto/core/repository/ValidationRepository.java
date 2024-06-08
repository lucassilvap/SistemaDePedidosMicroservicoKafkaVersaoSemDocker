package com.example.servicodevalidacaodeproduto.core.repository;

import com.example.servicodevalidacaodeproduto.core.entity.Validation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Long> {

    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
