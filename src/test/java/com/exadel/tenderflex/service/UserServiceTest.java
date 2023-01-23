package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
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
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBteWZpdC5jb20iLCJpYXQiOjE2NzM1MzE5MzEsImV4cCI6MTY3MzUzNTUzMX0.ncZiUNsJK1LFh2U59moFgWhzcWZyW3p0TL9O_hWVcvw";

    @Test
    void save() {
        // preconditions
        final User userInput = getPreparedUserInput();
        final User userOutput = getPreparedUserOutput();
        Mockito.when(userRepository.save(userInput)).thenReturn(userOutput);

        //test
        User actual = userService.save(userInput);

        // assert
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
    void get() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<User> page = new PageImpl<>(Collections.singletonList(userOutput), pageable, 1);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

        //test
        Page<User> actual = userService.get(pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.isFirst());
        for (User user : actual.getContent()) {
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
        User actual = userService.get(id);

        // assert
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
        User actual = userService.getUser(email);

        // assert
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
        UserDtoOutput actual = userService.saveDto(dtoInput);
        Mockito.verify(userValidator, Mockito.times(1)).validateEntity(actualUser.capture());
        Mockito.verify(roleService, Mockito.times(1)).assignRoles(actualUser.capture());

        // assert
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(actual);
        assertEquals(id.toString(), actual.getId());
        assertEquals(email, actual.getEmail());
        assertEquals(EUserRole.CONTRACTOR, actual.getRole());
        assertEquals(username, actual.getUsername());
        assertEquals(EUserStatus.ACTIVATED, actual.getStatus());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
    }

    @Test
    void getDto() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<User> page = new PageImpl<>(Collections.singletonList(userOutput), pageable, 1);
        final PageDtoOutput<UserDtoOutput> pageDtoOutput = getPreparedPageDtoOutput();
        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(userMapper.outputPageMapping(page)).thenReturn(pageDtoOutput);

        //test
        PageDtoOutput<UserDtoOutput> actual = userService.getDto(pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.getFirst());
        Assertions.assertTrue(actual.getLast());
        assertEquals(2, actual.getNumber());
        assertEquals(1, actual.getNumberOfElements());
        assertEquals(1, actual.getSize());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getTotalElements());
        for (UserDtoOutput user : actual.getContent()) {
            assertEquals(EUserRole.CONTRACTOR, user.getRole());
            assertEquals(id.toString(), user.getId());
            assertEquals(email, user.getEmail());
            assertEquals(username, user.getUsername());
            assertEquals(EUserStatus.ACTIVATED, user.getStatus());
            assertEquals(dtCreate, user.getDtCreate());
            assertEquals(dtUpdate, user.getDtUpdate());
        }
    }

    @Test
    void testGetDto() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        final UserDtoOutput userDtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userOutput));
        Mockito.when(userMapper.outputMapping(userOutput)).thenReturn(userDtoOutput);

        //test
        UserDtoOutput test = userService.getDto(id);

        // assert
        assertNotNull(test);
        assertEquals(EUserRole.CONTRACTOR, test.getRole());
        assertEquals(id.toString(), test.getId());
        assertEquals(email, test.getEmail());
        assertEquals(username, test.getUsername());
        assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        assertEquals(dtCreate, test.getDtCreate());
        assertEquals(dtUpdate, test.getDtUpdate());
    }

    @Test
    void updateDto() {
        // preconditions
        final User userInput = getPreparedUserOutput();
        final UserDtoInput dtoInput = getPreparedUserDtoInput();
        final UserDtoOutput dtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userMapper.inputMapping(dtoInput)).thenReturn(userInput);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userInput));
        Mockito.when(userRepository.save(userInput)).thenReturn(userInput);
        Mockito.when(userMapper.outputMapping(userInput)).thenReturn(dtoOutput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);

        //test
        UserDtoOutput actual = userService.updateDto(dtoInput, id, dtUpdate.toEpochMilli());
        Mockito.verify(userValidator, Mockito.times(1)).validateEntity(actualUser.capture());
        Mockito.verify(userValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualUser.capture());
        Mockito.verify(userMapper, Mockito.times(1)).updateEntityFields(actualUser.capture(),
                actualUser.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(actual);
        assertEquals(EUserRole.CONTRACTOR, actual.getRole());
        assertEquals(id.toString(), actual.getId());
        assertEquals(email, actual.getEmail());
        assertEquals(username, actual.getUsername());
        assertEquals(EUserStatus.ACTIVATED, actual.getStatus());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
    }

    @Test
    void saveUser() {
        // preconditions
        final User userInput = getPreparedUserOutput();
        final UserDtoRegistration dtoInput = getPreparedUserDtoRegistration();
        final UserLoginDtoOutput dtoOutput = getPreparedUserLoginDtoOutput();
        Mockito.when(userMapper.userInputMapping(dtoInput)).thenReturn(userInput);
        Mockito.when(userRepository.save(userInput)).thenReturn(userInput);
        Mockito.when(userMapper.registerOutputMapping(userInput)).thenReturn(dtoOutput);
        ArgumentCaptor<User> actualUser = ArgumentCaptor.forClass(User.class);

        //test
        UserLoginDtoOutput actual = userService.saveUser(dtoInput);
        Mockito.verify(userValidator, Mockito.times(1)).validateEntity(actualUser.capture());
        Mockito.verify(roleService, Mockito.times(1)).assignRoles(actualUser.capture());

        // assert
        assertEquals(userInput, actualUser.getValue());
        assertNotNull(actual);
        assertEquals(email, actual.getEmail());
        assertEquals(token, actual.getToken());
    }

    @Test
    void getUserDto() {
        // preconditions
        final User userOutput = getPreparedUserOutput();
        final UserDtoOutput userDtoOutput = getPreparedUserDtoOutput();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userOutput);
        Mockito.when(userMapper.outputMapping(userOutput)).thenReturn(userDtoOutput);

        //test
        UserDtoOutput actual = userService.getUserDto(email);

        // assert
        assertNotNull(actual);
        assertEquals(EUserRole.CONTRACTOR, actual.getRole());
        assertEquals(id.toString(), actual.getId());
        assertEquals(email, actual.getEmail());
        assertEquals(username, actual.getUsername());
        assertEquals(EUserStatus.ACTIVATED, actual.getStatus());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
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
    UserDtoRegistration getPreparedUserDtoRegistration() {
        return UserDtoRegistration.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(EUserRole.CONTRACTOR).build();
    }

    UserLoginDtoOutput getPreparedUserLoginDtoOutput() {
        return UserLoginDtoOutput.builder()
                .email(email)
                .token(token).build();
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

    PageDtoOutput<UserDtoOutput> getPreparedPageDtoOutput() {
        return PageDtoOutput.<UserDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singletonList(getPreparedUserDtoOutput()))
                .build();
    }
}