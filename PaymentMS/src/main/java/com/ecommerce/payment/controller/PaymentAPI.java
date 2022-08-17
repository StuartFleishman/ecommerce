package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.CardDTO;
import com.ecommerce.payment.dto.OrderDTO;
import com.ecommerce.payment.dto.TransactionDTO;
import com.ecommerce.payment.dto.TransactionStatus;
import com.ecommerce.payment.service.PaymentCircuitBreakerService;
import com.ecommerce.payment.service.PaymentService;
import com.ecommerce.payment.utility.PayOrderFallbackException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequestMapping(value = "/payment-api")
@RestController
@CrossOrigin
@Validated
@EnableAutoConfiguration
@CircuitBreaker(name = "payForOrder")
public class PaymentAPI {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate template;

    private static final Log LOGGER = LogFactory.getLog(PaymentAPI.class);

    @Autowired
    private PaymentCircuitBreakerService paymentCircuitBreakerService;

    @PostMapping(value = "/customer/{customerEmailId}/cards")
    public ResponseEntity<String> addNewCard(@RequestBody CardDTO cardDTO,
                                             @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                                                     message = "{invalid.email.format}")
                                             @PathVariable("customerEmailId") String customerEmailId)
            throws Exception, NoSuchAlgorithmException {
        LOGGER.info("Received request to add new  card for customer : " + cardDTO.getCustomerEmailId());

        int cardId;
        cardId = paymentService.addCustomerCard(customerEmailId, cardDTO);
        String message = environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS");
        String toReturn = message + cardId;
        toReturn = toReturn.trim();
        return new ResponseEntity<>(toReturn, HttpStatus.OK);

    }

    @PutMapping(value = "/update/card")
    public ResponseEntity<String> updateCustomerCard(@Valid @RequestBody CardDTO cardDTO)
            throws Exception, NoSuchAlgorithmException {
        LOGGER.info("Received request to update  card :" + cardDTO.getCardId() + " of customer : "
                + cardDTO.getCustomerEmailId());

        paymentService.updateCustomerCard(cardDTO);
        String modificationSuccessMsg = environment.getProperty("PaymentAPI.UPDATE_CARD_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    @DeleteMapping(value = "/customer/{customerEmailId}/card/{cardID}/delete")
    public ResponseEntity<String> deleteCustomerCard(@PathVariable("cardID") Integer cardID,
                                                     @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                                                             message = "{invalid.email.format}")
                                                     @PathVariable("customerEmailId") String customerEmailId)
            throws Exception {
        LOGGER.info("Received request to delete  card :" + cardID + " of customer : " + customerEmailId);

        paymentService.deleteCustomerCard(customerEmailId, cardID);
        String modificationSuccessMsg = environment.getProperty("PaymentAPI.CUSTOMER_CARD_DELETED_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    @GetMapping(value = "/customer/{customerEmailId}/card-type/{cardType}")
    public ResponseEntity<List<CardDTO>> getCardsOfCustomer(@PathVariable String customerEmailId,
                                                            @PathVariable String cardType)
            throws Exception {
        List<CardDTO> cardDTOS = paymentService.getCustomerCardOfCardType(customerEmailId, cardType);
        return new ResponseEntity<>(cardDTOS, HttpStatus.OK);
    }

    @CircuitBreaker(name = "payForOrder", fallbackMethod = "payForOrderFallback")
    @PostMapping(value = "/customer/{customerEmailId}/pay-order")
    public ResponseEntity<String> payForOrder(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable String customerEmailId,
            @Valid @RequestBody TransactionDTO transactionDTO)
            throws NoSuchAlgorithmException, Exception, PayOrderFallbackException {
        OrderDTO orderDTO =
                template.getForEntity("http://CUSTOMERMS" + "/customerorder-api/order/"
                + transactionDTO.getOrder().getOrderId(), OrderDTO.class).getBody();
        assert orderDTO != null;
        transactionDTO.setTransactionDate(orderDTO.getDateOfOrder());
        transactionDTO.setTotalPrice(orderDTO.getTotalPrice());
        transactionDTO.setOrder(orderDTO);
        TransactionDTO transaction = paymentService.authenticatePayment(customerEmailId,transactionDTO);
        int id = paymentService.addTransaction(transaction);
        paymentCircuitBreakerService.updateOrderAfterPayment(transaction.getOrder().getOrderId(),
                transaction.getTransactionStatus().toString());
        String message = "Order Placed Successfully with id: " + id;
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public ResponseEntity<String> payForOrderFallback(String customerEmailId, TransactionDTO transactionDTO,
                                                      RuntimeException exception) {
        String message = null;
        if(exception.getMessage().equals(environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING")))
            message = environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
        message = environment.getProperty("PaymentAPI.PAYMENT_FAILURE_FALLBACK");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}