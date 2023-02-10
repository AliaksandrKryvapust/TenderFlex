package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.UserPageForAdminDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

@WebMvcTest(controllers = UserAdminController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc
class UserAdminControllerTest {
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

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    final LocalDate dtLogin = LocalDate.parse("04/04/2023", df);
    final String dt_login = "04/04/2023";
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final String id = "1d63d7df-f1b3-4e92-95a3-6c7efad96901";
    final String password = "kdrL556D";

    @Test
    @WithMockUser(username = "admin@tenderflex.com", password = "kdrL556D", roles = {"ADMIN"})
    void getPage() throws Exception {
        // preconditions
        final PageDtoOutput<UserPageForAdminDtoOutput> pageDtoOutput = getPreparedPageForAdminDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").descending());
        Mockito.when(userManager.getDtoForAdmin(pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].dt_login").value(dt_login))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id));

        //test
        Mockito.verify(userManager, Mockito.times(1)).getDtoForAdmin(pageable);
    }

    @Test
    @WithMockUser(username = "admin@tenderflex.com", password = "kdrL556D", roles = {"ADMIN"})
    void get() throws Exception {
        // preconditions
        final UserDtoOutput dtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userManager.getDto(UUID.fromString(id))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(EUserStatus.ACTIVATED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

        //test
        Mockito.verify(userManager, Mockito.times(1)).getDto(UUID.fromString(id));
    }

    @Test
    @WithMockUser(username = "admin@tenderflex.com", password = "kdrL556D", roles = {"ADMIN"})
    void post() throws Exception {
        // preconditions
        final UserDtoInput dtoInput = getPreparedUserDtoInput();
        final UserDtoOutput dtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userManager.saveDto(dtoInput)).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(EUserStatus.ACTIVATED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

        //test
        Mockito.verify(userManager, Mockito.times(1)).saveDto(dtoInput);
    }

    @Test
    @WithMockUser(username = "admin@tenderflex.com", password = "kdrL556D", roles = {"ADMIN"})
    void put() throws Exception {
        // preconditions
        final UserDtoInput dtoInput = getPreparedUserDtoInput();
        final UserDtoOutput dtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userManager.updateDto(dtoInput, UUID.fromString(id), dtUpdate.toEpochMilli())).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/admin/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(EUserStatus.ACTIVATED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

        //test
        Mockito.verify(userManager, Mockito.times(1)).updateDto(dtoInput, UUID.fromString(id), dtUpdate.toEpochMilli());
    }

    UserPageForAdminDtoOutput getPreparedUserForAdminDtoOutput() {
        return UserPageForAdminDtoOutput.builder()
                .id(id)
                .email(email)
                .role(EUserRole.CONTRACTOR)
                .dtLogin(dtLogin)
                .build();
    }

    PageDtoOutput<UserPageForAdminDtoOutput> getPreparedPageForAdminDtoOutput() {
        return PageDtoOutput.<UserPageForAdminDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singleton(getPreparedUserForAdminDtoOutput()))
                .build();
    }

    UserDtoOutput getPreparedUserDtoOutput() {
        return UserDtoOutput.builder()
                .id(id)
                .email(email)
                .username(username)
                .role(EUserRole.CONTRACTOR)
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    UserDtoInput getPreparedUserDtoInput() {
        return UserDtoInput.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(EUserRole.CONTRACTOR.name())
                .status(EUserStatus.ACTIVATED.name())
                .build();
    }
}