package com.example.servicodepagamento.core.service;

import com.example.servicodepagamento.core.dto.Event;
import com.example.servicodepagamento.core.dto.History;
import com.example.servicodepagamento.core.dto.OrderProduct;
import com.example.servicodepagamento.core.entity.Payment;  
import com.example.servicodepagamento.core.enums.ESagaStatus;
import com.example.servicodepagamento.core.producer.KafkaProducer;
import com.example.servicodepagamento.core.repository.PaymentRepo;
import com.example.servicodepagamento.core.ultils.JsonUltil;
import com.example.servicodepagamento.kafka.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.servicodepagamento.core.enums.EpaymentStatus.REFUND;
import static com.example.servicodepagamento.core.enums.EpaymentStatus.SUCESS;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Component
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_SUM_VALUE =0.0;
    private static final Double MIN_AMOUNT_VALUE = 0.1;
    @Autowired
    private final JsonUltil jsonUltil;

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private PaymentRepo paymentRepository;

    private void createPendingPayment(Event event) {
        var totalAmout = calculateAmount(event);
        var totalItems = calculateTotalItems(event);
        var payment = Payment.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .totalAmount(totalAmout)
                .totalItems(totalItems)
                .build();
        save(payment);
        setEventAmountItems(event, payment);
    }

    private double calculateAmount(Event event){
        return event
                .getPayload()
                .getOrderProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private int calculateTotalItems(Event  event){
        return event
                .getPayload()
                .getOrderProducts()
                .stream()
                .map(OrderProduct::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }

    private Payment findByOrderIdAndTransactionId(Event event){
        return paymentRepository.findByOrderIdAndTransactionId(event.getPayload().getId(),
                event.getTransactionId()).orElseThrow(() -> new ValidationException("Payment not found" +
                "by OrderId and TransactionId"));
    }

    private void save(Payment payment){
        paymentRepository.save(payment);
    }

    private void setEventAmountItems(Event event, Payment payment){
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private void validateAmount(Double amount) {
        if(amount < MIN_AMOUNT_VALUE) {
            throw new ValidationException("The minium amount available is ".concat(MIN_AMOUNT_VALUE.toString()));
        }
    }

    public void realizePayment(Event event) {
        try{
           checkCurrentValidation(event);
           createPendingPayment(event);
           var payment = findByOrderIdAndTransactionId(event);
           validateAmount(payment.getTotalAmount());
           changePaymentToSucess(payment);
           handleSucess(event);
        }catch (Exception e){
            log.error("Error trying to make payment", e);
            log.error(e.getCause().toString());
            handleFailCurrentNotExecuted(event, e.getMessage());
        }
        assert jsonUltil != null;
        kafkaProducer.sendEvent(jsonUltil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(),
                event.getTransactionId())) {
            throw new ValidationException("There is another transactionId for this validation");
        }
    }

    private void changePaymentToSucess(Payment payment) {
        payment.setEpaymentStatus(SUCESS);
        save(payment);
    }

    private void handleSucess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Payment realized sucessfully");
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLlBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to realized payment ".concat(message));
    }

    public void realizeRefund(Event event) {
        
           event.setStatus(ESagaStatus.FAIL);
           event.setSource(CURRENT_SOURCE);
           try {
               changePaymentStatusToRefund(event);
               addHistory(event, "Rollback executed for payment");
           }catch (Exception e) {
               addHistory(event, "Rollback not executed for payment ".concat(e.getMessage()));
           }
        kafkaProducer.sendEvent(jsonUltil.toJson(event));
    }

    private void changePaymentStatusToRefund(Event event) {
        var payment = findByOrderIdAndTransactionId(event);
        payment.setEpaymentStatus(REFUND);
        setEventAmountItems(event, payment);
        save(payment);
    }

    private void addHistory(Event event, String messae) {
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(messae)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

}
