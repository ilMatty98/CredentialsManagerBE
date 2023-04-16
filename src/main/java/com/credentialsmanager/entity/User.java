package com.credentialsmanager.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Basic
    private String email;

    @Basic
    private String salt;

    @Basic
    private String hash;

    @Basic
    @Column(name = "protected_symmetric_key")
    private String protectedSymmetricKey;

    @Basic
    @Column(name = "initialization_vector")
    private String initializationVector;

    @Basic
    @Column(name = "timestamp_creation")
    private Timestamp timestampCreation;

    @Basic
    @Column(name = "timestamp_last_access")
    private Timestamp timestampLastAccess;

    @Basic
    private String language;
}
