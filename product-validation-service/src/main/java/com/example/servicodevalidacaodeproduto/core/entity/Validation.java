package com.example.servicodevalidacaodeproduto.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validation {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private boolean sucess;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist(){
        var now = LocalDateTime.now();
        createdAt = now;
        updateAt = now;
    }

    @PreUpdate
    public void preUpdate(){
        var now = LocalDateTime.now();
        updateAt = now;
    }



}
