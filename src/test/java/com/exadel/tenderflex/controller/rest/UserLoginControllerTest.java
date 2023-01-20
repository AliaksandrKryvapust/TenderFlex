package com.exadel.tenderflex.controller.rest;


import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.repository.entity.EUserRole;
import com.exadel.tenderflex.repository.entity.EUserStatus;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.IUserManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

@WebMvcTest(controllers = UserLoginController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc
class UserLoginControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private IUserManager userManager;
    @MockBean
    SecurityContext securityContext;
    // Beans for JwtFilter
    @MockBean
    private JwtTokenUtil tokenUtil;
    @MockBean
    private JwtUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "admin@tenderflex.com", password = "kdrL556D", roles = {"ADMIN"})
    void getCurrentUser() throws Exception {
        // preconditions
        final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
        final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
        final String email = "admin@tenderflex.com";
        final String username = "someone";
        final String id = "1d63d7df-f1b3-4e92-95a3-6c7efad96901";
        final UserDtoOutput userDtoOutput = UserDtoOutput.builder()
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .email(email)
                .username(username)
                .role(EUserRole.CONTRACTOR)
                .status(EUserStatus.ACTIVATED)
                .id(id)
                .build();
        Mockito.when(userManager.getUserDto(email)).thenReturn(userDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(EUserStatus.ACTIVATED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

        //test
        Mockito.verify(userManager).getUserDto(email);
    }

    @Test
    void login() throws Exception {
        // preconditions
        final String email = "admin@myfit.com";
        final String password = "kdrL556D";
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBteWZpdC5jb20iLCJpYXQiOjE2NzM1MzE5MzEsImV4cCI6MTY3MzUzNTUzMX0.ncZiUNsJK1LFh2U59moFgWhzcWZyW3p0TL9O_hWVcvw";
        final UserDtoLogin userDtoLogin = UserDtoLogin.builder()
                .email(email)
                .password(password).build();
        final UserLoginDtoOutput userLoginDtoOutput = UserLoginDtoOutput.builder()
                .email(email)
                .token(token).build();
        Mockito.when(userManager.login(userDtoLogin)).thenReturn(userLoginDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token));

        //test
        Mockito.verify(userManager).login(userDtoLogin);
    }

    @Test
    void registration() throws Exception {
        // preconditions
        final String username = "someone";
        final String email = "admin@myfit.com";
        final String password = "kdrL556D";
        final UserDtoRegistration userDtoRegistration = UserDtoRegistration.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(EUserRole.CONTRACTOR).build();
        final UserLoginDtoOutput userLoginDtoOutput = UserLoginDtoOutput.builder()
                .email(email)
                .build();
        Mockito.when(userManager.saveUser(userDtoRegistration)).thenReturn(userLoginDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/registration").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoRegistration)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        //test
        Mockito.verify(userManager).saveUser(userDtoRegistration);
    }
}