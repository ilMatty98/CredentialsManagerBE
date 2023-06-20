package com.credentialsmanager.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "logins")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credentials_id", nullable = false)
    private Credential credential;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "2fa")
    private boolean twoFactorAuthentication;
}
