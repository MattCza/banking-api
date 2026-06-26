package com.bank.controller;

import com.bank.dto.account.AccountResponse;
import com.bank.dto.account.CreateAccountRequest;
import com.bank.model.Account;
import com.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account createdAccount = accountService.createAccount(request);

        AccountResponse response = new AccountResponse(
                createdAccount.getId(),
                createdAccount.getOwnerName(),
                createdAccount.getEmail(),
                createdAccount.getBalance()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") Long id) {
        Account account = accountService.getAccountById(id);

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getOwnerName(),
                account.getEmail(),
                account.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountById(@PathVariable("id") Long id) {
        accountService.deleteAccountById(id);
    }

}
