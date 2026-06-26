package com.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "Owner name cannot be empty")
        @Length(min = 3, max = 50, message = "Owner name must be between 3 and 50 characters")
        String ownerName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be a valid format")
        // RFC 5321 defines max length of e-mail address to 254 chars -> SMTP limits email address to 254 chars.
        @Size(max = 254, message = "Email address cannot exceed 254 characters")
        String email,

        @NotNull(message = "Initial balance cannot be null")
        @PositiveOrZero(message = "Initial balance cannot be negative")
        @Digits(integer = 17, fraction = 2)
        BigDecimal initialBalance
) {
}
