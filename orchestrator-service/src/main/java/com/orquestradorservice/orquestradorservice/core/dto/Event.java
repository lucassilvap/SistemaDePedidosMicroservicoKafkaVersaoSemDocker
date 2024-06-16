package com.orquestradorservice.orquestradorservice.core.dto;

import com.orquestradorservice.orquestradorservice.core.enums.EEventSource;
import com.orquestradorservice.orquestradorservice.core.enums.ESagaStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String id;
    private String transactionId;
    private String orderId;
    private Order order;
    private EEventSource source;
    @Enumerated(EnumType.STRING)
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addToHistory(History history) {
        if (eventHistory.isEmpty()) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }
}