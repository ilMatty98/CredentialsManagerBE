package com.credentialsmanager.test;

import com.credentialsmanager.CredentialsManagerBeApplication;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.entity.User;
import com.credentialsmanager.mapper.AuthenticationMapper;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.service.EmailService;
import com.credentialsmanager.service.TokenJwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static com.credentialsmanager.constants.UrlConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CredentialsManagerBeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

    @Value("${token.public-key}")
    protected String tokenPublicKey;

    @Value("${mail.from}")
    protected String emailFrom;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected TokenJwtService tokenJwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthenticationMapper authenticationMapper;

    protected static GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);

    protected static final String MESSAGE = "$.message";

    @BeforeEach
    void startGreenEmail() {
        greenMail.start();
    }

    @AfterEach
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
                field.set(object, generateRandomString(random.nextInt(20)));
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

    protected String objectToJsonString(Object object) throws JsonProcessingException {
        var ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    @Deprecated(since = "Use signUp")
    protected User addUser(String email) {
        var user = fillObject(new User());
        user.setEmail(email);
        user.setLanguage("EN");
        return userRepository.save(user);
    }

    protected User signUp(String email, String password) throws Exception {
        var signUp = new SignUpDto();
        signUp.setEmail(email);
        signUp.setMasterPasswordHash(password);
        signUp.setInitializationVector("initVector");
        signUp.setProtectedSymmetricKey("protectedSymmetricKey");
        signUp.setLanguage("EN");

        var mockHttpServletRequestBuilder = post(BASE_PATH + SIGN_UP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isCreated());

        return userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }

    protected User confirmEmail(String email) throws Exception {
        var user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        var mockHttpServletRequestBuilder = patch(BASE_PATH + CONFIRM_EMAIL, email, user.getVerificationCode())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        return userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }

    protected String getTokenFromLogIn(String email, String password) throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(email);
        logIn.setIpAddress("1.1.1.1");
        logIn.setMasterPasswordHash(password);

        var mockHttpServletRequestBuilder = post(BASE_PATH + LOG_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        var mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        var jsonResponse = mvcResult.getResponse().getContentAsString();
        return JsonPath.parse(jsonResponse).read("$.token");
    }

    protected LocalDateTime getLocalDataTime(Timestamp timestamp) {
        return Optional.ofNullable(timestamp)
                .map(Timestamp::toLocalDateTime)
                .map(l -> l.truncatedTo(ChronoUnit.SECONDS))
                .orElse(null);
    }

}
