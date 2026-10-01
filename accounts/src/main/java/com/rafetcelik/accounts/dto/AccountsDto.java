package com.rafetcelik.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Accounts",
        description = "Account details"
)
public class AccountsDto {
    @Schema(
            description = "Account number", example = "1234567890"
    )
    @NotEmpty(message = "Account number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @Schema(
            description = "Account type", example = "Savings"
    )
    @NotEmpty(message = "Account type cannot be empty")
    private String accountType;

    @Schema(
            description = "Branch address", example = "123 Main Street"
    )
    @NotEmpty(message = "Branch address cannot be empty")
    private String branchAddress;
}
