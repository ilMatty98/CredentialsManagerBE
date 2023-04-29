package com.credentialsmanager.test;

import com.credentialsmanager.CredentialsManagerBeApplication;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.service.EmailService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CredentialsManagerBeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

    @Value("${mail.from}")
    protected String emailFrom;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthenticationMapper authenticationMapper;
    protected static GreenMail greenMail;

    @BeforeAll
    void startGreenEmail() {
        greenMail = new GreenMail(ServerSetup.SMTP);
        greenMail.start();
    }

    @AfterAll
    void stopGreenEmail() {
        greenMail.stop();
    }

    @BeforeEach
    void cleanRepository() {
        userRepository.deleteAll();
    }

    protected static String generateRandomString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        return new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @SneakyThrows
    protected static <T> T fillObject(T object) {
        var fields = object.getClass().getDeclaredFields();
        var random = new Random();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == int.class) {
                field.setInt(object, random.nextInt());
            } else if (field.getType() == long.class) {
                field.setLong(object, random.nextLong());
            } else if (field.getType() == double.class) {
                field.setDouble(object, random.nextDouble());
            } else if (field.getType() == float.class) {
                field.setFloat(object, random.nextFloat());
            } else if (field.getType() == boolean.class) {
                field.setBoolean(object, random.nextBoolean());
            } else if (field.getType() == char.class) {
                field.setChar(object, (char) (random.nextInt(26) + 'a'));
            } else if (field.getType() == String.class) {
                field.set(object, generateRandomString(random.nextInt(1000)));
            } else if (field.getType() == Timestamp.class) {
                field.set(object, Timestamp.from(Instant.now()));
            } else if (field.getType() == BigInteger.class) {
                field.set(object, BigInteger.valueOf(random.nextLong(1000)));
            } else if (field.getType() == UserStateEnum.class) {
                field.set(object, UserStateEnum.VERIFIED);
            }
        }
        return object;
    }

}
