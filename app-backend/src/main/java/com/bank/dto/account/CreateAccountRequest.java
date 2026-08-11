package com.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(

        // Validation
        @NotBlank(message = "Owner name must not be blank")
        @Size(min = 3, max = 50, message = "Owner name must be between 3 and 50 characters")
        String ownerName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be a valid format")
        @Size(max = 254, message = "Email must be at most 254 characters long")
        String email,

        @NotNull(message = "Initial balance cannot be null")
        @PositiveOrZero(message = "Initial balance cannot be negative")
        @Digits(integer = 17, fraction = 2, message = "Initial balance must contain up to 17 integer digits and 2 decimal places")
        BigDecimal initialBalance
) {
}
