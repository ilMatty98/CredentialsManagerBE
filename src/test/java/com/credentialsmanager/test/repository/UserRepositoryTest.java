package com.credentialsmanager.test.repository;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.entity.User;
import com.credentialsmanager.repository.UserRepository;
import com.credentialsmanager.test.ApiTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryTest extends ApiTest {

    private static final String EMAIL = "email@email.it";

    @Test
    void testExistsByEmail() throws Exception {
        assertFalse(userRepository.existsByEmail(EMAIL));
        signUp(EMAIL, PASSWORD);
        assertTrue(userRepository.existsByEmail(EMAIL));
    }

    @Test
    void testExistsByEmailOrNewEmail() throws Exception {
        assertFalse(userRepository.existsByEmailOrNewEmail(EMAIL));
        var user = signUp(EMAIL, PASSWORD);
        assertTrue(userRepository.existsByEmailOrNewEmail(EMAIL));

        user.setNewEmail(EMAIL + "2");
        userRepository.save(user);
        assertTrue(userRepository.existsByEmailOrNewEmail(user.getNewEmail()));
    }

    @Test
    void testFindByEmail() throws Exception {
        assertFalse(userRepository.findByEmail(EMAIL).isPresent());
        var user = signUp(EMAIL, PASSWORD);
        checkUser((userRepository) -> userRepository.findByEmail(EMAIL), user);
    }

    @Test
    void testFindByEmailAndState() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user.setState(UserStateEnum.UNVERIFIED);
        userRepository.save(user);

        assertFalse(userRepository.findByEmailAndState(EMAIL, UserStateEnum.VERIFIED).isPresent());

        user.setState(UserStateEnum.VERIFIED);
        userRepository.save(user);

        checkUser((userRepository) -> userRepository.findByEmailAndState(EMAIL, UserStateEnum.VERIFIED), user);
    }

    @Test
    void findByEmailAndNewEmailAndState() throws Exception {
        var newEmail = EMAIL + ".";
        var user = signUp(EMAIL, PASSWORD);
        user.setState(UserStateEnum.UNVERIFIED);
        user.setNewEmail(newEmail);
        userRepository.save(user);

        assertFalse(userRepository.findByEmailAndNewEmailAndState(EMAIL, newEmail, UserStateEnum.VERIFIED).isPresent());

        user.setState(UserStateEnum.VERIFIED);
        userRepository.save(user);

        checkUser((userRepository) -> userRepository.findByEmailAndNewEmailAndState(EMAIL, newEmail, UserStateEnum.VERIFIED), user);
    }

    @Test
    void testFindByEmailAndVerificationCode() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user.setVerificationCode("code1");
        userRepository.save(user);

        assertFalse(userRepository.findByEmailAndVerificationCode(EMAIL, "code").isPresent());

        user.setVerificationCode("code");
        userRepository.save(user);

        checkUser((userRepository) -> userRepository.findByEmailAndVerificationCode(EMAIL, "code"), user);
    }

    private void checkUser(Function<UserRepository, Optional<User>> userRepositoryFunction, User expectedUser) {
        userRepositoryFunction.apply(userRepository)
                .ifPresentOrElse(u -> {
                    assertEquals(expectedUser.getId(), u.getId());
                    assertEquals(expectedUser.getEmail(), u.getEmail());
                    assertEquals(expectedUser.getSalt(), u.getSalt());
                    assertEquals(expectedUser.getHash(), u.getHash());
                    assertEquals(expectedUser.getProtectedSymmetricKey(), u.getProtectedSymmetricKey());
                    assertEquals(expectedUser.getInitializationVector(), u.getInitializationVector());
                    assertEquals(getLocalDataTime(expectedUser.getTimestampCreation()), getLocalDataTime(u.getTimestampCreation()));
                    assertEquals(getLocalDataTime(expectedUser.getTimestampLastAccess()), getLocalDataTime(u.getTimestampLastAccess()));
                    assertEquals(getLocalDataTime(expectedUser.getTimestampPassword()), getLocalDataTime(u.getTimestampPassword()));
                    assertEquals(expectedUser.getLanguage(), u.getLanguage());
                    assertEquals(expectedUser.getHint(), u.getHint());
                    assertEquals(expectedUser.getPropic(), u.getPropic());
                    assertEquals(expectedUser.getState(), u.getState());
                    assertEquals(expectedUser.getVerificationCode(), u.getVerificationCode());
                }, Assert::fail);
    }

}
