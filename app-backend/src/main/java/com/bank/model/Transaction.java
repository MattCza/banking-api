package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Transaction(Long accountId, BigDecimal amount, Currency currency, TransactionType type) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Money getMoney() {
        return new Money(amount, currency);
    }
}
