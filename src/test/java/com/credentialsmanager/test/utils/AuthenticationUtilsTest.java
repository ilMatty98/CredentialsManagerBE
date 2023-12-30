package com.credentialsmanager.test.utils;

import com.credentialsmanager.test.CredentialsManagerTests;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static com.credentialsmanager.utils.AuthenticationUtils.generateArgon2id;
import static com.credentialsmanager.utils.AuthenticationUtils.generateSalt;
import static org.junit.Assert.*;

class AuthenticationUtilsTest extends CredentialsManagerTests {

    @Test
    void testGenerateSalt() {
        var saltSize = 128;
        var salt = generateSalt(saltSize);
        assertNotNull(salt);
        assertEquals(saltSize, salt.length);
    }

    @Test
    void testGenerateArgon2id() {
        var password = "password123";
        var salt = new byte[128];
        new SecureRandom().nextBytes(salt);
        int argon2idSize = 256;
        int iteration = 3;
        int memLimitKB = 64000;
        int parallelism = 4;

        var argon2id_1 = generateArgon2id(password, salt, argon2idSize, iteration, memLimitKB, parallelism);
        assertNotNull(argon2id_1);
        assertEquals(argon2idSize, argon2id_1.length);

        var argon2id_2 = generateArgon2id(password, salt, argon2idSize, iteration, memLimitKB, parallelism);
        assertArrayEquals(argon2id_1, argon2id_2);
    }

}
