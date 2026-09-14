package com.bank.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(

        @NotBlank(message = "Owner name must not be blank")
        @Size(min = 3, max = 50, message = "Owner name must be between 3 and 50 characters")
        String ownerName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be a valid format")
        @Size(max = 254, message = "Email must be at most 254 characters long")
        String email
) {
}
