package com.example.servicodeinventario.core.dto;

import com.example.servicodeinventario.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class History {

    private String source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;


}
