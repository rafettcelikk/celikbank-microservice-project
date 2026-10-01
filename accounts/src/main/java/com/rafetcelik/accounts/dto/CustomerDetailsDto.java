package com.rafetcelik.accounts.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonPropertyOrder({ "name", "email", "mobileNumber", "accountsDto" })
@Schema(
        name = "Customer Details",
        description = "Customer, Accounts, Loans and Cards details"
)
public class CustomerDetailsDto {
    @Schema(
            description = "Name of the customer", example = "John Doe"
    )
    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(
            description = "Email address of the customer", example = "john.doe@example.com"
    )
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be a valid email address")
    private String email;

    @Schema(
            description = "Mobile number of the customer", example = "1234567890"
    )
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
            description = "Accounts details of the customer"
    )
    private AccountsDto accountsDto;

    @Schema(
            description = "Cards details of the customer"
    )
    private CardsDto cardsDto;

    @Schema(
            description = "Loans details of the customer"
    )
    private LoansDto loansDto;
}
