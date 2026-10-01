package com.rafetcelik.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(
        name = "Loans",
        description = "Loan information"
)
public class LoansDto {
    @Schema(
            description = "Mobile number of the customer", example = "1234567890"
    )
    @NotEmpty(message = "Mobile number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
            description = "Loan number", example = "100646930341"
    )
    @NotEmpty(message = "Loan number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{12})", message = "Loan number must be 12 digits")
    private String loanNumber;

    @Schema(
            description = "Type of the loan", example = "Home Loan"
    )
    @NotEmpty(message = "Loan type cannot be empty")
    private String loanType;

    @Schema(
            description = "Total loan amount", example = "100000"
    )
    @Positive(message = "Total loan must be a positive number")
    private int totalLoan;

    @Schema(
            description = "Amount paid towards the loan", example = "20000"
    )
    @PositiveOrZero(message = "Amount paid must be a positive number or zero")
    private int amountPaid;

    @Schema(
            description = "Outstanding amount of the loan", example = "80000"
    )
    @PositiveOrZero(message = "Outstanding amount must be a positive number or zero")
    private int outstandingAmount;
}