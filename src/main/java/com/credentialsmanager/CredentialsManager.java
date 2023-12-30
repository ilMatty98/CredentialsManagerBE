package com.credentialsmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CredentialsManager {

    public static void main(String[] args) {
        SpringApplication.run(CredentialsManager.class, args);
    }

}
