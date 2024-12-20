package com.credentialsmanager.repository;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndState(String email, UserStateEnum state);

    Optional<User> findByEmailAndNewEmailAndState(String email, String newEmail, UserStateEnum state);

    Optional<User> findByEmailAndVerificationCode(String email, String code);
}
