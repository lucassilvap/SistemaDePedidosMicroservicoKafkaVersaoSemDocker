package com.example.servicodevalidacaodeproduto.core.service;
import com.example.servicodevalidacaodeproduto.config.exception.ValidationException;
import com.example.servicodevalidacaodeproduto.core.dto.History;
import com.example.servicodevalidacaodeproduto.core.dto.OrderProduct;
import com.example.servicodevalidacaodeproduto.core.entity.Validation;
import com.example.servicodevalidacaodeproduto.core.enums.ESagaStatus;
import com.example.servicodevalidacaodeproduto.core.producer.KafkaProducer;
import com.example.servicodevalidacaodeproduto.core.repository.ProductRepository;
import com.example.servicodevalidacaodeproduto.core.repository.ValidationRepository;
import com.example.servicodevalidacaodeproduto.core.ultils.JsonUltil;
import com.example.servicodevalidacaodeproduto.core.dto.Event;
import org.springframework.util.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor

public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUltil jsonUltil;
    private KafkaProducer kafkaProducer;
    private ProductRepository productRepository;
    private ValidationRepository validationRepository;

    public void validateExistingProducts(Event event){
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSucess(event);

        }catch (Exception ex){
            log.error("Error trying to validate products: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        kafkaProducer.sendEvent(jsonUltil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event,  "fail to validate products ".concat(message));

    }

    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "rollback executed on product validation!");
        kafkaProducer.sendEvent(jsonUltil.toJson(event));
    }

    private void changeValidationToFail(Event event) {
        validationRepository.findByOrderIdAndTransactionId(event.getOrder().getId(), event.getTransactionId())
                .ifPresentOrElse(validation -> {
                            validation.setSucess(false);
                            validationRepository.save(validation);
                        },
                        () -> {
                            createValidation(event, false);
                        });
    }

    private void handleSucess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products validated sucessefuly!");
    }


    private void addHistory(com.example.servicodevalidacaodeproduto.core.dto.Event event, String message) {
        var history = History
                .builder()
                .source(String.valueOf(event.getSource()))
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void validateProductsInformed(Event event) {
        if (ObjectUtils.isEmpty(event.getOrder()) || ObjectUtils.isEmpty(event.getOrder().getProducts())){
            throw  new ValidationException("Product list is empty");
        }
        if (ObjectUtils.isEmpty(event.getOrder().getId()) || ObjectUtils.isEmpty(event.getOrder().getTransactionId())){
            throw  new ValidationException("OrderId and TransactionId must be informed!");
        }
    }

    private void checkCurrentValidation(Event event) {
       validateProductsInformed(event);
       if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())){
           throw new ValidationException("There is another transactionId for this validation");
       }
       event.getOrder().getProducts().forEach(product ->{
           validateProductInformed(product);
           validateExistingProduct(product.getProduct().getCode());
       });
    }

    private void validateProductInformed(OrderProduct orderProduct) {
        if (ObjectUtils.isEmpty(orderProduct) || ObjectUtils.isEmpty(orderProduct.getProduct().getCode())){
            throw new ValidationException("Product must be informed!");
        }
    }

    private void validateExistingProduct(String code){
        if (!productRepository.existsByCode(code)){
            throw new ValidationException("Product does not exists in database");
        }
    }

    private void createValidation(Event event, boolean sucess){
           var validation = Validation
                   .builder()
                   .orderId(event.getOrder().getId())
                   .transactionId(event.getTransactionId())
                   .sucess(sucess)
                   .build();
           validationRepository.save(validation);
    }

}
