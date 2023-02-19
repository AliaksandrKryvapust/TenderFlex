package com.exadel.tenderflex.service;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.cache.CacheStorage;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.Role;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.repository.entity.enums.ERolePrivilege;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
import com.exadel.tenderflex.service.validator.api.IUserDetailsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
    @Mock
    private CacheStorage<Object> tokenBlackList;

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
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userOutput));

        //test
        UserDetails actual = jwtUserDetailsService.loadUserByUsername(email);

        // assert
        assertNotNull(actual);
        assertNotNull(actual.getAuthorities());
        assertEquals(email, actual.getUsername());
        assertEquals(password, actual.getPassword());
        assertEquals(2, actual.getAuthorities().size());
    }

    @Test
    void login() {
        // preconditions
        final UserDtoLogin dtoInput = getPreparedUserDtoLogin();
        final UserLoginDtoOutput dtoOutput = getPreparedUserLoginDtoOutput();
        final User userOutput = getPreparedUserOutput();
        final UserDetails userDetails = getPreparedUserDetails();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userOutput));
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userOutput));
        when(userMapper.loginOutputMapping(userDetails, token)).thenReturn(dtoOutput);
        ArgumentCaptor<UserDtoLogin> actualUserDtoLogin = ArgumentCaptor.forClass(UserDtoLogin.class);
        ArgumentCaptor<UserDetails> actualUserDetails = ArgumentCaptor.forClass(UserDetails.class);

        //test
        UserLoginDtoOutput actual = jwtUserDetailsService.login(dtoInput);
        Mockito.verify(userDetailsValidator, Mockito.times(1)).validateLogin(actualUserDtoLogin.capture(),
                actualUserDetails.capture());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));

        // assert
        assertNotNull(actual);
        assertEquals(email, actual.getEmail());
        assertEquals(token, actual.getToken());
    }

    @Test
    void logout() {
        // preconditions
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(AUTHORIZATION)).thenReturn(token);

        //test
        jwtUserDetailsService.logout(request);
        Mockito.verify(tokenBlackList, Mockito.times(1)).add(ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(Object.class));
    }

    @Test
    void tokenIsInBlackList() {
        // preconditions
        when(tokenBlackList.get(token)).thenReturn(token);
        ArgumentCaptor<String> actualToken = ArgumentCaptor.forClass(String.class);

        //test
        boolean actual = jwtUserDetailsService.tokenIsInBlackList(token);
        Mockito.verify(tokenBlackList, Mockito.times(1)).get(actualToken.capture());

        // assert
        assertEquals(token, actualToken.getValue());
        assertTrue(actual);
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

    UserDtoLogin getPreparedUserDtoLogin() {
        return UserDtoLogin.builder()
                .email(email)
                .password(password)
                .build();
    }

    UserLoginDtoOutput getPreparedUserLoginDtoOutput() {
        return UserLoginDtoOutput.builder()
                .email(email)
                .role(EUserRole.CONTRACTOR.name())
                .token(token)
                .duration(JwtTokenUtil.JWT_TOKEN_VALID_TIME)
                .build();
    }

    UserDetails getPreparedUserDetails() {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" +EUserRole.CONTRACTOR.name()));
        authorityList.add(new SimpleGrantedAuthority(ERolePrivilege.CAN_READ_OFFER.name()));
        return new org.springframework.security.core.userdetails.User(email, password, true, true,
                true, true, authorityList);
    }
}