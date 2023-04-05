package com.credentialsmanager.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String email;

    @Basic
    private String hash;

    @Basic
    private String salt;
}
