package com.example.servicodepagamento.core.consumer;
import com.example.servicodepagamento.core.service.PaymentService;
import com.example.servicodepagamento.core.ultils.JsonUltil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private final JsonUltil jsonUltil;
    private final PaymentService paymentService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${payment-sucess.spring.kafka.template.default-topic}"
    )
    public void consumerSucessEvent(String payload){
        log.info("Receiving event {} from payment-validation-sucess topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
        try{
            paymentService.realizePayment(event);
        }catch (Exception e){
            Throwable throwable;
            throwable = e.getCause();
            if(throwable != null){
                log.error(e.getCause().toString());
            }
        }
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${payment-fail.spring.kafka.template.default-topic}"
    )
    public void consumerFailEvent(String payload){
        log.info("Receiving rollback event {} from payment-validation-fail topic", payload);
        var event = jsonUltil.toEvent(payload);
        log.info(event.toString());
        try{
            paymentService.realizeRefund(event);
        }catch(Exception e){
            Throwable throwable;
            throwable = e.getCause();
            if(throwable != null){
                log.error(e.getCause().toString());
            }
        }
    }
}
