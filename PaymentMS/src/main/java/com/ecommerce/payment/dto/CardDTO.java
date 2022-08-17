package com.ecommerce.payment.dto;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CardDTO {
    private Integer cardId;
    private String cardType;
    @NotNull(message = "{transaction.cardNumber.notPresent}")
    private String cardNumber;
    private String nameOnCard;
    private String hashCvv;
    @NotNull(message = "{transaction.cvv.notPresent}")
    private Integer cvv;
    private String expiryDate;
    private String customerEmailId;
}