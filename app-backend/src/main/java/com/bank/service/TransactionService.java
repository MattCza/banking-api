package com.bank.service;

import com.bank.dto.transaction.TransactionRequest;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.Money;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction deposit(Long accountId, TransactionRequest request) {
        Account account = findAccountForUpdate(accountId);

        Money depositMoney = new Money(request.amount(), request.currency());
        Money newBalance = account.getMoney().add(depositMoney);

        account.setBalance(newBalance.amount());
        accountRepository.saveAndFlush(account);

        Transaction transaction = new Transaction(accountId, request.amount(), request.currency(), TransactionType.DEPOSIT);
        return transactionRepository.saveAndFlush(transaction);
    }

    @Transactional
    public Transaction withdraw(Long accountId, TransactionRequest request) {
        Account account = findAccountForUpdate(accountId);

        Money withdrawalMoney = new Money(request.amount(), request.currency());
        Money newBalance = account.getMoney().subtract(withdrawalMoney);

        if (newBalance.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Account with id " + accountId + " has insufficient funds.");
        }

        account.setBalance(newBalance.amount());
        accountRepository.saveAndFlush(account);

        Transaction transaction = new Transaction(accountId, request.amount(), request.currency(), TransactionType.WITHDRAWAL);
        return transactionRepository.saveAndFlush(transaction);
    }

    private Account findAccountForUpdate(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found."));
    }
}
