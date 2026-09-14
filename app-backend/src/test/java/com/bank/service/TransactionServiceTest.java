package com.bank.service;

import com.bank.dto.transaction.TransactionRequest;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.CurrencyMismatchException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account(BigDecimal balance, Currency currency) {
        Account account = new Account("John Doe", "john@doe.xyz", balance, currency);
        account.setId(10L);
        return account;
    }

    @Test
    void shouldDeposit_WhenAccountAndCurrencyAreValid() {
        Account account = account(new BigDecimal("100.00"), Currency.PLN);
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), Currency.PLN);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);
        when(transactionRepository.saveAndFlush(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.deposit(10L, request);

        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
        assertThat(result.getAccountId()).isEqualTo(10L);
        assertThat(result.getAmount()).isEqualByComparingTo("50.00");
        assertThat(result.getCurrency()).isEqualTo(Currency.PLN);
        assertThat(result.getType()).isEqualTo(TransactionType.DEPOSIT);
    }

    @Test
    void shouldThrowCurrencyMismatchException_WhenDepositCurrencyDiffersFromAccount() {
        Account account = account(new BigDecimal("100.00"), Currency.PLN);
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), Currency.USD);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.deposit(10L, request))
                .isInstanceOf(CurrencyMismatchException.class);

        verify(accountRepository, never()).saveAndFlush(any(Account.class));
        verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
    }

    @Test
    void shouldThrowAccountNotFoundException_WhenDepositingToMissingAccount() {
        when(accountRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), Currency.PLN);

        assertThatThrownBy(() -> transactionService.deposit(99L, request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldWithdraw_WhenBalanceIsSufficient() {
        Account account = account(new BigDecimal("100.00"), Currency.PLN);
        TransactionRequest request = new TransactionRequest(new BigDecimal("40.00"), Currency.PLN);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);
        when(transactionRepository.saveAndFlush(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.withdraw(10L, request);

        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
        assertThat(result.getType()).isEqualTo(TransactionType.WITHDRAWAL);
    }

    @Test
    void shouldWithdraw_WhenAmountEqualsExactBalance() {
        Account account = account(new BigDecimal("100.00"), Currency.PLN);
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), Currency.PLN);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));
        when(accountRepository.saveAndFlush(account)).thenReturn(account);
        when(transactionRepository.saveAndFlush(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.withdraw(10L, request);

        assertThat(account.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldThrowInsufficientFundsException_WhenWithdrawalExceedsBalance() {
        Account account = account(new BigDecimal("100.00"), Currency.PLN);
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.01"), Currency.PLN);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw(10L, request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("10");

        verify(accountRepository, never()).saveAndFlush(accountCaptor.capture());
        verify(transactionRepository, never()).saveAndFlush(any(Transaction.class));
        assertThat(account.getBalance())
                .as("balance must stay unchanged when the withdrawal is rejected")
                .isEqualByComparingTo("100.00");
    }

    @Test
    void shouldThrowCurrencyMismatchException_WhenWithdrawalCurrencyDiffersFromAccount() {
        Account account = account(new BigDecimal("100.00"), Currency.EUR);
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), Currency.GBP);

        when(accountRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.withdraw(10L, request))
                .isInstanceOf(CurrencyMismatchException.class);

        verify(accountRepository, never()).saveAndFlush(any(Account.class));
    }

    @Test
    void shouldThrowAccountNotFoundException_WhenWithdrawingFromMissingAccount() {
        when(accountRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), Currency.PLN);

        assertThatThrownBy(() -> transactionService.withdraw(99L, request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }
}
