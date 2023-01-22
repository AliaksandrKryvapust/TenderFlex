package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.service.api.IRoleService;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleService roleService;
    @Mock
    private IUserValidator userValidator;
    @Mock
    private UserMapper userMapper;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "admin@tenderflex.com";
    final String username = "someone";
    final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
    final String password = "kdrL556D";

    @Test
    void save() {
        // preconditions
        final User userInput = getPreparedUserInput();
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userRepository.save(userInput)).thenReturn(userOutput);

        //test
        User test = userService.save(userInput);

        // assert
        assertNotNull(test);
        assertNotNull(test.getRoles());
        assertEquals(id, test.getId());
        assertEquals(email, test.getEmail());
        assertEquals(password, test.getPassword());
        assertEquals(username, test.getUsername());
        assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        assertEquals(dtCreate, test.getDtCreate());
        assertEquals(dtUpdate, test.getDtUpdate());
        for (Role roles : test.getRoles()) {
            assertNotNull(roles.getPrivileges());
            assertEquals(id, roles.getId());
            assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            assertEquals(dtCreate, roles.getDtCreate());
            assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                assertEquals(id, privileges.getId());
                assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                assertEquals(dtCreate, privileges.getDtCreate());
                assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    @Test
    void get() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<User> page = new PageImpl<>(Collections.singletonList(userOutput), pageable, 1);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

        //test
        Page<User> test = userService.get(pageable);

        // assert
        assertNotNull(test);
        assertEquals(1, test.getTotalPages());
        Assertions.assertTrue(test.isFirst());
        for (User user : test.getContent()) {
            assertNotNull(user.getRoles());
            assertEquals(id, user.getId());
            assertEquals(email, user.getEmail());
            assertEquals(password, user.getPassword());
            assertEquals(username, user.getUsername());
            assertEquals(EUserStatus.ACTIVATED, user.getStatus());
            assertEquals(dtCreate, user.getDtCreate());
            assertEquals(dtUpdate, user.getDtUpdate());
            for (Role roles : user.getRoles()) {
                assertNotNull(roles.getPrivileges());
                assertEquals(id, roles.getId());
                assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
                assertEquals(dtCreate, roles.getDtCreate());
                assertEquals(dtUpdate, roles.getDtUpdate());
                for (Privilege privileges : roles.getPrivileges()) {
                    assertEquals(id, privileges.getId());
                    assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                    assertEquals(dtCreate, privileges.getDtCreate());
                    assertEquals(dtUpdate, privileges.getDtUpdate());
                }
            }
        }
    }

    @Test
    void testGet() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userOutput));

        //test
        User test = userService.get(id);

        // assert
        assertNotNull(test);
        assertNotNull(test.getRoles());
        assertEquals(id, test.getId());
        assertEquals(email, test.getEmail());
        assertEquals(password, test.getPassword());
        assertEquals(username, test.getUsername());
        assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        assertEquals(dtCreate, test.getDtCreate());
        assertEquals(dtUpdate, test.getDtUpdate());
        for (Role roles : test.getRoles()) {
            assertNotNull(roles.getPrivileges());
            assertEquals(id, roles.getId());
            assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            assertEquals(dtCreate, roles.getDtCreate());
            assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                assertEquals(id, privileges.getId());
                assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                assertEquals(dtCreate, privileges.getDtCreate());
                assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    @Test
    void update() {
        // preconditions
        final User userInput = getPreparedUserOutput();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userInput));
        Mockito.when(userRepository.save(userInput)).thenReturn(userInput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);

        //test
        User actual = userService.update(userInput, id, dtUpdate.toEpochMilli());
        Mockito.verify(userValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualUser.capture());
        Mockito.verify(userMapper, Mockito.times(1)).updateEntityFields(actualUser.capture(),
                actualUser.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(actual);
        assertNotNull(actual.getRoles());
        assertEquals(id, actual.getId());
        assertEquals(email, actual.getEmail());
        assertEquals(password, actual.getPassword());
        assertEquals(username, actual.getUsername());
        assertEquals(EUserStatus.ACTIVATED, actual.getStatus());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        for (Role roles : actual.getRoles()) {
            assertNotNull(roles.getPrivileges());
            assertEquals(id, roles.getId());
            assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            assertEquals(dtCreate, roles.getDtCreate());
            assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                assertEquals(id, privileges.getId());
                assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                assertEquals(dtCreate, privileges.getDtCreate());
                assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    @Test
    void getUser() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userOutput);

        //test
        User test = userService.getUser(email);

        // assert
        assertNotNull(test);
        assertNotNull(test.getRoles());
        assertEquals(id, test.getId());
        assertEquals(email, test.getEmail());
        assertEquals(password, test.getPassword());
        assertEquals(username, test.getUsername());
        assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        assertEquals(dtCreate, test.getDtCreate());
        assertEquals(dtUpdate, test.getDtUpdate());
        for (Role roles : test.getRoles()) {
            assertNotNull(roles.getPrivileges());
            assertEquals(id, roles.getId());
            assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            assertEquals(dtCreate, roles.getDtCreate());
            assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                assertEquals(id, privileges.getId());
                assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                assertEquals(dtCreate, privileges.getDtCreate());
                assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    @Test
    void saveDto() {
        // preconditions
        final UserDtoInput dtoInput = getPreparedUserDtoInput();
        final UserDtoOutput dtoOutput = getPreparedUserDtoOutput();
        final User userInput = getPreparedUserInput();
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userMapper.inputMapping(dtoInput)).thenReturn(userInput);
        Mockito.when(userRepository.save(userInput)).thenReturn(userOutput);
        Mockito.when(userMapper.outputMapping(userOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);

        //test
        UserDtoOutput test = userService.saveDto(dtoInput);
        Mockito.verify(userValidator, Mockito.times(1)).validateEntity(actualUser.capture());
        Mockito.verify(roleService, Mockito.times(1)).setRoles(actualUser.capture());

        // assert
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(test);
        assertEquals(id.toString(), test.getId());
        assertEquals(email, test.getEmail());
        assertEquals(EUserRole.CONTRACTOR, test.getRole());
        assertEquals(username, test.getUsername());
        assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        assertEquals(dtCreate, test.getDtCreate());
        assertEquals(dtUpdate, test.getDtUpdate());
    }

    @Test
    void getDto() {
    }

    @Test
    void testGetDto() {
    }

    @Test
    void updateDto() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void getUserDto() {
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

    UserDtoInput getPreparedUserDtoInput() {
        return UserDtoInput.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(EUserRole.CONTRACTOR.name())
                .status(EUserStatus.ACTIVATED.name()).build();
    }

    UserDtoOutput getPreparedUserDtoOutput() {
        return UserDtoOutput.builder()
                .id(id.toString())
                .email(email)
                .username(username)
                .role(EUserRole.CONTRACTOR)
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }
}