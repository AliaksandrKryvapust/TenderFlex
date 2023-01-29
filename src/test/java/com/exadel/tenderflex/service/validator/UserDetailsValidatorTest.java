package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.Role;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.repository.entity.enums.ERolePrivilege;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserDetailsValidatorTest {
    @InjectMocks
    private UserDetailsValidator userDetailsValidator;
    @Mock
    private PasswordEncoder passwordEncoder;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
    final String password = "kdrL556D";

    @Test
    void validateIncorrectLogin() {
        // preconditions
        final UserDtoLogin user = getPreparedUserDtoLogin();
        final UserDetails userDetails = getPreparedUserDetails();
        final String messageExpected = "User login or password is incorrect or user is not activated";

        //test
        Exception actualException = assertThrows(BadCredentialsException.class,
                () -> userDetailsValidator.validateLogin(user, userDetails));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
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
                .dtUpdate(dtUpdate).build();
    }

    UserDetails getPreparedUserDetails() {
        User user = getPreparedUserOutput();
        Set<GrantedAuthority> authorityList = new HashSet<>();
        user.getRoles().forEach((i) -> {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + i.getRoleType().name()));
            for (Privilege privilege : i.getPrivileges()) {
                authorityList.add(new SimpleGrantedAuthority(privilege.getPrivilege().name()));
            }
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true,
                true, true, true, authorityList);
    }

    UserDtoLogin getPreparedUserDtoLogin() {
        return UserDtoLogin.builder()
                .email(email)
                .password(password).build();
    }
}