package com.bank.controller;

import com.bank.dto.transaction.TransactionRequest;
import com.bank.dto.transaction.TransactionResponse;
import com.bank.model.Money;
import com.bank.model.Transaction;
import com.bank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts/{id}")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable("id") Long id, @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.deposit(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable("id") Long id, @Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.withdraw(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(transaction));
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                new Money(transaction.getAmount(), transaction.getCurrency()),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }
}
