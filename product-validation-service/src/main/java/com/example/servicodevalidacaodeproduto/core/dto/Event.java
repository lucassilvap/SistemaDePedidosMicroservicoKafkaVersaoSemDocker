package com.example.servicodevalidacaodeproduto.core.dto;

import com.example.servicodevalidacaodeproduto.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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
    private Order payload;
    private String source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addToHistory(History history){
        if(StringUtils.isEmpty(history)){
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);

    }
}
