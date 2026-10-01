package com.rafetcelik.accounts.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(
        name = "Cards",
        description = "Card information"
)
@JsonPropertyOrder({ "mobileNumber", "cardNumber", "cardType", "totalLimit", "amountUsed", "availableAmount" })
public class CardsDto {
    @Schema(
            description = "Mobile number", example = "1234567890"
    )
    @NotEmpty(message = "Mobile number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
            description = "Card number", example = "1234567890123456"
    )
    @NotEmpty(message = "Card number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{16})", message = "Card number must be 16 digits")
    private String cardNumber;

    @Schema(
            description = "Card type", example = "Credit"
    )
    @NotEmpty(message = "Card type cannot be empty")
    private String cardType;

    @Positive(message = "Total limit must be a positive number")
    @Schema(
            description = "Total limit", example = "100000"
    )
    private int totalLimit;

    @PositiveOrZero(message = "Amount used must be a positive number or zero")
    @Schema(
            description = "Amount used", example = "1000"
    )
    private int amountUsed;

    @PositiveOrZero(message = "Available amount must be a positive number or zero")
    @Schema(
            description = "Available amount", example = "50000"
    )
    private int availableAmount;
}
