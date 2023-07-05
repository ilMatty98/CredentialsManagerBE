package com.credentialsmanager.repository;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = :email OR u.newEmail = :email")
    boolean existsByEmailOrNewEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndState(String email, UserStateEnum state);

    Optional<User> findByEmailAndNewEmailAndState(String email, String newEmail, UserStateEnum state);

    Optional<User> findByEmailAndVerificationCode(String email, String code);
}
