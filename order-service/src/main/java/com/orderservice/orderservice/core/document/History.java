package com.orderservice.orderservice.core.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class History {

    private String source;
    private String status;
    private String message;
    private LocalDateTime createdAt;


}
