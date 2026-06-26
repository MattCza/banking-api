package com.bank.service;

import com.bank.dto.account.CreateAccountRequest;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateEmailException;
import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        Account account = new Account(
                request.ownerName().trim(),
                normalizedEmail,
                request.initialBalance()
        );

        try {
            return accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException("An account with email '" + normalizedEmail + "' already exists.");
        }
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " not found."));
    }

    @Transactional
    public void deleteAccountById(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account with id " + id + " not found.");
        }

        accountRepository.deleteById(id);
    }

}
