package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String ownerName;

    @Column(length = 254, nullable = false, unique = true)
    private String email;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    public Account(String ownerName, String email, BigDecimal balance, Currency currency) {
        this.ownerName = ownerName;
        this.email = email;
        this.balance = balance;
        this.currency = currency;
    }

    public Money getMoney() {
        return new Money(balance, currency);
    }
}
