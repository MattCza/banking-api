package com.bank.service;

import com.bank.dto.account.CreateAccountRequest;
import com.bank.dto.account.UpdateAccountRequest;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateEmailException;
import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("An account with email '" + normalizedEmail + "' already exists.");
        }

        Account account = new Account(
                request.ownerName().trim(),
                normalizedEmail,
                request.initialBalance());

        try {
            return accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateEmailViolation(ex)) {
                throw new DuplicateEmailException("An account with email '" + normalizedEmail + "' already exists.");
            }

            throw ex;
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

    @Transactional
    public Account updateAccount(Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " not found."));

        String normalizedEmail = normalizeEmail(request.email());

        if (accountRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new DuplicateEmailException("Account with email '" + normalizedEmail + "' already exists.");
        }

        account.setOwnerName(request.ownerName().trim());
        account.setEmail(normalizedEmail);
        account.setBalance(request.balance());

        try {
            return accountRepository.saveAndFlush(account);
        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateEmailViolation(ex)) {
                throw new DuplicateEmailException("An account with email '" + normalizedEmail + "' already exists.");
            }

            throw ex;
        }
    }






    private boolean isDuplicateEmailViolation(DataIntegrityViolationException ex) {
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();

        if (message == null) {
            return false;
        }

        String normalizedMessage = message.toLowerCase(Locale.ROOT);

        // Constraint names may be returned in lowercase, so match the normalized value
        return normalizedMessage.contains("uk_accounts_email");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
