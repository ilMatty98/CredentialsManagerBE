package com.credentialsmanager.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class AuthenticationUtils {

    public static String getSalt(int saltSize) {
        var salt = new byte[saltSize];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    @SneakyThrows
    public static String getHash(String password, String salt, int iterationCount, int keyLength, String algorithm) {
        var keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), iterationCount, keyLength);
        var secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        var hash = secretKeyFactory.generateSecret(keySpec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    @SneakyThrows
    public static byte[] generateHashForPassword(String password, String salt, String pepper, String algorithm, int iterationCount, int keyLength) {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterationCount, keyLength);
        byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey key = new SecretKeySpec(pepper.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
//        byte[] cipherText = cipher.doFinal(hash);
        return cipher.doFinal(hash);
//        return Base64.getEncoder()
//                .encodeToString(cipherText);
    }

}
