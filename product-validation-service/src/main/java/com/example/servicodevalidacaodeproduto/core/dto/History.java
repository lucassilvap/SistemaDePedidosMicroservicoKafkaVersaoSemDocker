package com.example.servicodevalidacaodeproduto.core.dto;

import com.example.servicodevalidacaodeproduto.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {

    private String source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;


}
