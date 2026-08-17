package com.bank.service;

import com.bank.dto.account.CreateAccountRequest;
import com.bank.dto.account.UpdateAccountRequest;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DuplicateEmailException;
import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount_WithTrimmedOwnerNameAndNormalizedEmail() {
        CreateAccountRequest request = new CreateAccountRequest(
                "  John Doe  ",
                "  John.Doe@Example.com  ",
                new BigDecimal("100.50")
        );

        when(accountRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(invocation -> {
            Account saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Account result = accountService.createAccount(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOwnerName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getBalance()).isEqualByComparingTo("100.50");
    }

    @Test
    void shouldThrowDuplicateEmailException_WhenCreatingAccountWithExistingEmail() {
        CreateAccountRequest request = new CreateAccountRequest(
                "John Doe",
                "John.Doe@Example.com",
                new BigDecimal("100.50")
        );

        when(accountRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("john.doe@example.com");

        verify(accountRepository, never()).saveAndFlush(any(Account.class));
    }

    @Test
    void shouldUpdateAccount_WhenDataIsValid() {
        Account existing = new Account("John Doe", "john@doe.xyz", new BigDecimal("100.00"));
        existing.setId(10L);

        UpdateAccountRequest request = new UpdateAccountRequest(
                "  Jane Doe  ",
                "  Jane@Doe.xyz  ",
                new BigDecimal("250.75")
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(accountRepository.existsByEmailAndIdNot("jane@doe.xyz", 10L)).thenReturn(false);
        when(accountRepository.saveAndFlush(existing)).thenReturn(existing);

        Account result = accountService.updateAccount(10L, request);

        assertThat(result.getOwnerName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane@doe.xyz");
        assertThat(result.getBalance()).isEqualByComparingTo("250.75");
    }

    @Test
    void shouldThrowDuplicateEmailException_WhenUpdatingAccountWithAnotherAccountsEmail() {
        Account existing = new Account("John Doe", "john@doe.xyz", new BigDecimal("100.00"));
        existing.setId(10L);

        UpdateAccountRequest request = new UpdateAccountRequest(
                "Jane Doe",
                "taken@doe.xyz",
                new BigDecimal("250.75")
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(accountRepository.existsByEmailAndIdNot("taken@doe.xyz", 10L)).thenReturn(true);

        assertThatThrownBy(() -> accountService.updateAccount(10L, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("taken@doe.xyz");

        verify(accountRepository, never()).saveAndFlush(any(Account.class));
    }

    @Test
    void shouldThrowAccountNotFoundException_WhenGettingMissingAccount() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldThrowAccountNotFoundException_WhenDeletingMissingAccount() {
        when(accountRepository.existsById(77L)).thenReturn(false);

        assertThatThrownBy(() -> accountService.deleteAccountById(77L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("77");
    }
}
