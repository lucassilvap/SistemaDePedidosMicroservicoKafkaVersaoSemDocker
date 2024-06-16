package com.orquestradorservice.orquestradorservice.core.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    private String id;
    private List<OrderProduct> products;
    private LocalDateTime localDateTime;
    private String transactionId;
    private double totalAmount;
    private int totalItems;
}
