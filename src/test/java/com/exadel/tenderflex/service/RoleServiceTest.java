package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IRoleRepository;
import com.exadel.tenderflex.repository.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @InjectMocks
    private RoleService roleService;
    @Mock
    private IRoleRepository roleRepository;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
    final String password = "kdrL556D";

    @Test
    void setRoles() {
        // preconditions
        final User userInput = getPreparedUserInput();
        final Role role = getPreparedRoleOutput();
        Mockito.when(roleRepository.getRoleByRoleType(EUserRole.CONTRACTOR)).thenReturn(Optional.of(role));
        ArgumentCaptor<EUserRole> actualUserRole = ArgumentCaptor.forClass(EUserRole.class);

        //test
        roleService.setRoles(userInput);
        Mockito.verify(roleRepository, Mockito.times(1)).getRoleByRoleType(actualUserRole.capture());

        // assert
        assertEquals(EUserRole.CONTRACTOR, actualUserRole.getValue());
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
                .status(EUserStatus.ACTIVATED).build();
    }

    Role getPreparedRoleOutput() {
        final Privilege privilege = Privilege.builder()
                .id(id)
                .privilege(ERolePrivilege.CAN_READ_OFFER)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        return Role.builder()
                .id(id)
                .roleType(EUserRole.CONTRACTOR)
                .privileges(new HashSet<>(Collections.singleton(privilege)))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }
}