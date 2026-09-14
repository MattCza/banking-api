package com.bank.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldAdd_WhenCurrenciesMatch() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PLN);
        Money second = new Money(new BigDecimal("50.00"), Currency.PLN);

        Money result = first.add(second);

        assertThat(result.amount()).isEqualByComparingTo("150.00");
        assertThat(result.currency()).isEqualTo(Currency.PLN);
    }

    @Test
    void shouldSubtract_WhenCurrenciesMatch() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PLN);
        Money second = new Money(new BigDecimal("30.00"), Currency.PLN);

        Money result = first.subtract(second);

        assertThat(result.amount()).isEqualByComparingTo("70.00");
        assertThat(result.currency()).isEqualTo(Currency.PLN);
    }

    @Test
    void shouldThrowCurrencyMismatchException_WhenAddingDifferentCurrencies() {
        Money pln = new Money(new BigDecimal("100.00"), Currency.PLN);
        Money usd = new Money(new BigDecimal("50.00"), Currency.USD);

        assertThatThrownBy(() -> pln.add(usd))
                .isInstanceOf(com.bank.exception.CurrencyMismatchException.class);
    }

    @Test
    void shouldThrowCurrencyMismatchException_WhenSubtractingDifferentCurrencies() {
        Money eur = new Money(new BigDecimal("100.00"), Currency.EUR);
        Money gbp = new Money(new BigDecimal("50.00"), Currency.GBP);

        assertThatThrownBy(() -> eur.subtract(gbp))
                .isInstanceOf(com.bank.exception.CurrencyMismatchException.class);
    }
}
