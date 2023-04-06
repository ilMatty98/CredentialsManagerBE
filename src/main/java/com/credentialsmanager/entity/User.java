package com.credentialsmanager.entity;


import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "users")
public class User {

    @Id
    private String email;

    @Basic
    private String hash;

    @Basic
    private String salt;
}