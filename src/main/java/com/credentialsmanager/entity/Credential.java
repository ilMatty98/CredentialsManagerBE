package com.credentialsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", length = 11, nullable = false)
    private String type;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "notes", length = 10000)
    private String notes;

    @Column(name = "timestamp_creation", nullable = false)
    private Timestamp timestampCreation;

    @Column(name = "timestamp_updated", nullable = false)
    private Timestamp timestampUpdated;
}
