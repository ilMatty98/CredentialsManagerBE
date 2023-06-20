package com.credentialsmanager.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credentials_id", nullable = false)
    private Credential credentials;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "number")
    private String number;

    @Column(name = "expiration")
    private String expiration;

    @Column(name = "cvv")
    private String cvv;
}
