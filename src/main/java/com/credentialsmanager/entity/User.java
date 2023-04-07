package com.credentialsmanager.entity;


import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity(name = "users")
public class User {

    @Id
    private String email;

    @Basic
    private String hash;

    @Basic
    private String salt;

    @Basic
    @Column(name = "timestamp_creation")
    private Timestamp timestampCreation;

    @Basic
    @Column(name = "timestamp_last_access")
    private Timestamp timestampLastAccess;

    @Basic
    private String token;
}
