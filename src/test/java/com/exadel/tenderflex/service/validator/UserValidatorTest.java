package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.ERolePrivilege;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    @InjectMocks
    private UserValidator userValidator;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
    final String password = "kdrL556D";

    @Test
    void validateNonEmptyId() {
        // preconditions
        final User user = getPreparedUserOutput();
        final String messageExpected = "User id should be empty for user: " + user;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankUsername() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setUsername(" ");
        final String messageExpected = "username is not valid for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortUsername() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setUsername("a");
        final String messageExpected = "username should contain from 2 to 50 letters for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankPassword() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setPassword(" ");
        final String messageExpected = "password is not valid for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortPassword() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setPassword("a");
        final String messageExpected = "password should contain from 2 to 200 letters for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankEmail() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setEmail(" ");
        final String messageExpected = "email is not valid for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortEmail() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setEmail("a");
        final String messageExpected = "email should contain from 2 to 50 letters for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyRole() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setRoles(null);
        final String messageExpected = "user role is not valid for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyStatus() {
        // preconditions
        final User user = getPreparedUserInput();
        user.setStatus(null);
        final String messageExpected = "user status is not valid for user:" + user;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> userValidator.validateEntity(user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void optimisticLockCheck() {
        // preconditions
        final User user = getPreparedUserOutput();
        final Long version = dtUpdate.toEpochMilli() - 1000;
        final String messageExpected = "user table update failed, version does not match update denied";

        //test
        Exception actualException = assertThrows(OptimisticLockException.class, () ->
                userValidator.optimisticLockCheck(version, user));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    User getPreparedUserInput() {
        final Role role = Role.builder()
                .id(id)
                .roleType(EUserRole.CONTRACTOR)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        return User.builder()
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .build();
    }

    User getPreparedUserOutput() {
        final Privilege privilege = Privilege.builder()
                .id(id)
                .privilege(ERolePrivilege.CAN_READ_OFFER)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        final Role role = Role.builder()
                .id(id)
                .roleType(EUserRole.CONTRACTOR)
                .privileges(new HashSet<>(Collections.singleton(privilege)))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }
}