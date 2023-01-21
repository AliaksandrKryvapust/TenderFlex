package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.service.api.IRoleService;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

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

    @Test
    void save() {
        // preconditions
        final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
        final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
        final String email = "admin@tenderflex.com";
        final String username = "someone";
        final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
        final String password = "kdrL556D";
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
        final User userInput = User.builder()
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED).build();
        final User userOutput = User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
        Mockito.when(userRepository.save(userInput)).thenReturn(userOutput);

        //test
        User test = userService.save(userInput);

        // assert
        Assertions.assertNotNull(test);
        Assertions.assertNotNull(test.getRoles());
        Assertions.assertEquals(id, test.getId());
        Assertions.assertEquals(email, test.getEmail());
        Assertions.assertEquals(password, test.getPassword());
        Assertions.assertEquals(username, test.getUsername());
        Assertions.assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        Assertions.assertEquals(dtCreate, test.getDtCreate());
        Assertions.assertEquals(dtUpdate, test.getDtUpdate());
        for (Role roles : test.getRoles()) {
            Assertions.assertNotNull(roles.getPrivileges());
            Assertions.assertEquals(id, roles.getId());
            Assertions.assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            Assertions.assertEquals(dtCreate, roles.getDtCreate());
            Assertions.assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                Assertions.assertEquals(id, privileges.getId());
                Assertions.assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                Assertions.assertEquals(dtCreate, privileges.getDtCreate());
                Assertions.assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    @Test
    void get() {
        // preconditions
        final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
        final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
        final String email = "admin@tenderflex.com";
        final String username = "someone";
        final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
        final String password = "kdrL556D";
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
        final User userOutput = User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<User> page = new PageImpl<>(Collections.singletonList(userOutput),pageable, 1);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

        //test
        Page<User> test = userService.get(pageable);

        // assert
        Assertions.assertNotNull(test);
        Assertions.assertEquals(1, test.getTotalPages());
        Assertions.assertTrue(test.isFirst());
        for (User user : test.getContent()) {
            Assertions.assertNotNull(user.getRoles());
            Assertions.assertEquals(id, user.getId());
            Assertions.assertEquals(email, user.getEmail());
            Assertions.assertEquals(password, user.getPassword());
            Assertions.assertEquals(username, user.getUsername());
            Assertions.assertEquals(EUserStatus.ACTIVATED, user.getStatus());
            Assertions.assertEquals(dtCreate, user.getDtCreate());
            Assertions.assertEquals(dtUpdate, user.getDtUpdate());
            for (Role roles : user.getRoles()) {
                Assertions.assertNotNull(roles.getPrivileges());
                Assertions.assertEquals(id, roles.getId());
                Assertions.assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
                Assertions.assertEquals(dtCreate, roles.getDtCreate());
                Assertions.assertEquals(dtUpdate, roles.getDtUpdate());
                for (Privilege privileges : roles.getPrivileges()) {
                    Assertions.assertEquals(id, privileges.getId());
                    Assertions.assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                    Assertions.assertEquals(dtCreate, privileges.getDtCreate());
                    Assertions.assertEquals(dtUpdate, privileges.getDtUpdate());
                }
            }
        }
    }

    @Test
    void testGet() {
        // preconditions
        final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
        final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
        final String email = "admin@tenderflex.com";
        final String username = "someone";
        final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
        final String password = "kdrL556D";
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
        final User userOutput = User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userOutput));

        //test
        User test = userService.get(id);

        // assert
        Assertions.assertNotNull(test);
        Assertions.assertNotNull(test.getRoles());
        Assertions.assertEquals(id, test.getId());
        Assertions.assertEquals(email, test.getEmail());
        Assertions.assertEquals(password, test.getPassword());
        Assertions.assertEquals(username, test.getUsername());
        Assertions.assertEquals(EUserStatus.ACTIVATED, test.getStatus());
        Assertions.assertEquals(dtCreate, test.getDtCreate());
        Assertions.assertEquals(dtUpdate, test.getDtUpdate());
        for (Role roles : test.getRoles()) {
            Assertions.assertNotNull(roles.getPrivileges());
            Assertions.assertEquals(id, roles.getId());
            Assertions.assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
            Assertions.assertEquals(dtCreate, roles.getDtCreate());
            Assertions.assertEquals(dtUpdate, roles.getDtUpdate());
            for (Privilege privileges : roles.getPrivileges()) {
                Assertions.assertEquals(id, privileges.getId());
                Assertions.assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
                Assertions.assertEquals(dtCreate, privileges.getDtCreate());
                Assertions.assertEquals(dtUpdate, privileges.getDtUpdate());
            }
        }
    }

    // TODO didn't manage to deal with AopContext.currentProxy()
//    @Test
//    void update() {
//        // preconditions
//        final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
//        final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
//        final String email = "admin@tenderflex.com";
//        final String username = "someone";
//        final UUID id = UUID.fromString("1d63d7df-f1b3-4e92-95a3-6c7efad96901");
//        final String password = "kdrL556D";
//        final Privilege privilege = Privilege.builder()
//                .id(id)
//                .privilege(ERolePrivilege.CAN_READ_OFFER)
//                .dtCreate(dtCreate)
//                .dtUpdate(dtUpdate)
//                .build();
//        final Role role = Role.builder()
//                .id(id)
//                .roleType(EUserRole.CONTRACTOR)
//                .privileges(new HashSet<>(Collections.singleton(privilege)))
//                .dtCreate(dtCreate)
//                .dtUpdate(dtUpdate)
//                .build();
//        final User userOutput = User.builder()
//                .id(id)
//                .email(email)
//                .password(password)
//                .username(username)
//                .roles(new HashSet<>(Collections.singleton(role)))
//                .status(EUserStatus.ACTIVATED)
//                .dtCreate(dtCreate)
//                .dtUpdate(dtUpdate).build();
//        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(userOutput));
//        Mockito.when(userRepository.save(userOutput)).thenReturn(userOutput);
//
//        //test
//        User test = userService.update(userOutput, id, dtUpdate.toEpochMilli());
//
//        // assert
//        Mockito.verify(userValidator, Mockito.times(1)).optimisticLockCheck(any(Long.class),
//                any(User.class));
//        Mockito.verify(userMapper, Mockito.times(1)).updateEntityFields(any(User.class),
//                any(User.class));
//        Assertions.assertNotNull(test);
//        Assertions.assertNotNull(test.getRoles());
//        Assertions.assertEquals(id, test.getId());
//        Assertions.assertEquals(email, test.getEmail());
//        Assertions.assertEquals(password, test.getPassword());
//        Assertions.assertEquals(username, test.getUsername());
//        Assertions.assertEquals(EUserStatus.ACTIVATED, test.getStatus());
//        Assertions.assertEquals(dtCreate, test.getDtCreate());
//        Assertions.assertEquals(dtUpdate, test.getDtUpdate());
//        for (Role roles : test.getRoles()) {
//            Assertions.assertNotNull(roles.getPrivileges());
//            Assertions.assertEquals(id, roles.getId());
//            Assertions.assertEquals(EUserRole.CONTRACTOR, roles.getRoleType());
//            Assertions.assertEquals(dtCreate, roles.getDtCreate());
//            Assertions.assertEquals(dtUpdate, roles.getDtUpdate());
//            for (Privilege privileges : roles.getPrivileges()) {
//                Assertions.assertEquals(id, privileges.getId());
//                Assertions.assertEquals(ERolePrivilege.CAN_READ_OFFER, privileges.getPrivilege());
//                Assertions.assertEquals(dtCreate, privileges.getDtCreate());
//                Assertions.assertEquals(dtUpdate, privileges.getDtUpdate());
//            }
//        }
//    }

    @Test
    void getUser() {
    }

    @Test
    void saveDto() {
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
}