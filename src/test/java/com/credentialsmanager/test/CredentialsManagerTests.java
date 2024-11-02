package com.credentialsmanager.test;

import com.credentialsmanager.CredentialsManager;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ChangeEmailDto;
import com.credentialsmanager.dto.request.LogInDto;
import com.credentialsmanager.dto.request.SignUpDto;
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
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CredentialsManager.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class CredentialsManagerTests extends ApiTestConstants {

    @Value("${token.expiration-minutes}")
    protected long tokenExpiration;

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

    private static Random random;

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

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @SneakyThrows
    protected static <T> T fillObject(T object) {
        var superclass = object.getClass().getSuperclass();
        if (superclass != null && !superclass.equals(Object.class))
            fillObject(object, superclass);

        fillObject(object, object.getClass());
        return object;
    }

    @SneakyThrows
    private static void fillObject(Object object, Class<?> clazz) {
        var fields = clazz.getDeclaredFields();
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
                field.set(object, generateRandomString(random.nextInt(20) + 1));
            } else if (field.getType() == Timestamp.class) {
                field.set(object, Timestamp.from(Instant.now()));
            } else if (field.getType() == BigInteger.class) {
                field.set(object, BigInteger.valueOf(random.nextLong(1000)));
            } else if (field.getType() == UserStateEnum.class) {
                field.set(object, UserStateEnum.VERIFIED);
            }
        }
    }

    protected static String objectToJsonString(Object object) throws JsonProcessingException {
        var ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    protected User signUp(String email, String password) throws Exception {
        var signUp = new SignUpDto();
        signUp.setEmail(email);
        signUp.setMasterPasswordHash(password);
        signUp.setInitializationVector("initVector");
        signUp.setProtectedSymmetricKey("protectedSymmetricKey");
        signUp.setLanguage(EN);
        signUp.setHint("Hint");
        signUp.setPropic("Propic");

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isCreated());

        return userRepository.findByEmail(email).orElseGet(Assertions::fail);
    }

    protected User confirmEmail(String email) throws Exception {
        var user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, email, user.getVerificationCode())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        return userRepository.findByEmail(email).orElseGet(Assertions::fail);
    }

    protected User changeEmail(String email, String password, String newEmail) throws Exception {
        var changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setEmail(newEmail);
        changeEmailDto.setMasterPasswordHash(password);

        var mockHttpServletRequestBuilder = put(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        return userRepository.findByEmail(email).orElseGet(Assertions::fail);
    }

    protected String getTokenFromLogIn(String email, String password) throws Exception {
        var logIn = fillObject(new LogInDto());
        logIn.setEmail(email);
        logIn.setIpAddress(IP_ADDRESS);
        logIn.setMasterPasswordHash(password);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        var mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        var jsonResponse = mvcResult.getResponse().getContentAsString();
        return JsonPath.parse(jsonResponse).read("$.token");
    }

    protected static LocalDateTime getLocalDataTime(Timestamp timestamp) {
        return Optional.ofNullable(timestamp)
                .map(Timestamp::toLocalDateTime)
                .map(l -> l.truncatedTo(ChronoUnit.SECONDS))
                .orElse(null);
    }

    protected static String createLargeString(double mb) {
        var desiredSizeInBytes = mb * 1024 * 1024; // 3 MB
        var chunkSize = 1024; // Chunk size for each iteration
        var chunkCount = desiredSizeInBytes / chunkSize;

        var sb = new StringBuilder();

        // Generate a chunk of characters to fill the StringBuilder
        var chunk = new String(new char[chunkSize]).replace("\0", "x");

        sb.append(chunk.repeat((int) chunkCount));

        return sb.toString();
    }

}
