package com.exadel.tenderflex.service;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.service.validator.api.IUserDetailsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceTest {
    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private IUserDetailsValidator userDetailsValidator;
    @Mock
    private UserMapper userMapper;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
    final String password = "kdrL556D";
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBteWZpdC5jb20iLCJpYXQiOjE2NzM1MzE5MzEsImV4cCI6MTY3MzUzNTUzMX0.ncZiUNsJK1LFh2U59moFgWhzcWZyW3p0TL9O_hWVcvw";

    @Test
    void loadUserByUsername() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userOutput);
        ArgumentCaptor<String> actualEmail = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);

        //test
        UserDetails actual = jwtUserDetailsService.loadUserByUsername(email);
        Mockito.verify(userDetailsValidator, Mockito.times(1)).validate(actualEmail.capture(),
                actualUser.capture());

        // assert
        assertEquals(email, actualEmail.getValue());
        assertEquals(userOutput, actualUser.getValue());
        assertNotNull(actual);
        assertNotNull(actual.getAuthorities());
        assertEquals(email, actual.getUsername());
        assertEquals(password, actual.getPassword());
        assertEquals(2, actual.getAuthorities().size());
    }

    @Test
    void login() {
        // preconditions
        final User userInput = getPreparedUserOutput();
        final UserDtoLogin dtoInput = getPreparedUserDtoLogin();
        final UserLoginDtoOutput dtoOutput = getPreparedUserLoginDtoOutput();
        final User userOutput = getPreparedUserOutput();
        final UserDetails userDetails = getPreparedUserDetails();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userOutput);
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);
        Mockito.when(userMapper.loginOutputMapping(userDetails, token)).thenReturn(dtoOutput);
        ArgumentCaptor<String> actualEmail = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<UserDtoLogin> actualUserDtoLogin = ArgumentCaptor.forClass(UserDtoLogin.class);
        ArgumentCaptor<UserDetails> actualUserDetails = ArgumentCaptor.forClass(UserDetails.class);

        //test
        UserLoginDtoOutput actual = jwtUserDetailsService.login(dtoInput);
        Mockito.verify(userDetailsValidator, Mockito.times(1)).validate(actualEmail.capture(),
                actualUser.capture());
        Mockito.verify(userDetailsValidator, Mockito.times(1)).validateLogin(actualUserDtoLogin.capture(),
                actualUserDetails.capture());

        // assert
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(actual);
        assertEquals(email, actual.getEmail());
        assertEquals(token, actual.getToken());
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

    UserDtoLogin getPreparedUserDtoLogin() {
        return UserDtoLogin.builder()
                .email(email)
                .password(password).build();
    }

    UserLoginDtoOutput getPreparedUserLoginDtoOutput() {
        return UserLoginDtoOutput.builder()
                .email(email)
                .token(token).build();
    }

    UserDetails getPreparedUserDetails() {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" +EUserRole.CONTRACTOR.name()));
        authorityList.add(new SimpleGrantedAuthority(ERolePrivilege.CAN_READ_OFFER.name()));
        return new org.springframework.security.core.userdetails.User(email, password, true, true,
                true, true, authorityList);
    }
}