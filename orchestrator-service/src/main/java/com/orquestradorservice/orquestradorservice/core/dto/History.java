package com.orquestradorservice.orquestradorservice.core.dto;

import com.orquestradorservice.orquestradorservice.core.enums.EEventSource;
import com.orquestradorservice.orquestradorservice.core.enums.ESagaStatus;
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

    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;


}
