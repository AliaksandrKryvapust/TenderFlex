package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserRegistrationDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.UserPageForAdminDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.api.IRoleService;
import com.exadel.tenderflex.service.api.IUserManager;
import com.exadel.tenderflex.service.api.IUserService;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, IUserManager {
    private final IUserRepository userRepository;
    private final IRoleService roleService;
    private final IUserValidator userValidator;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> get(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User update(User user, UUID id, Long version) {
        User currentEntity = get(id);
        userValidator.optimisticLockCheck(version, currentEntity);
        userMapper.updateEntityFields(user, currentEntity);
        return save(currentEntity);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public UserDtoOutput saveDto(UserDtoInput userDtoInput) {
        User entityToSave = userMapper.inputMapping(userDtoInput);
        userValidator.validateEntity(entityToSave);
        roleService.assignRoles(entityToSave);
        User user = save(entityToSave);
        return userMapper.outputMapping(user);
    }

    @Override
    public PageDtoOutput<UserDtoOutput> getDto(Pageable pageable) {
        Page<User> page = get(pageable);
        return userMapper.outputPageMapping(page);
    }

    @Override
    public UserDtoOutput getDto(UUID id) {
        User menu = get(id);
        return userMapper.outputMapping(menu);
    }

    @Override
    public UserDtoOutput updateDto(UserDtoInput dtoInput, UUID id, Long version) {
        User entityToSave = userMapper.inputMapping(dtoInput);
        userValidator.validateEntity(entityToSave);
        User user = update(entityToSave, id, version);
        return userMapper.outputMapping(user);
    }

    @Override
    public UserRegistrationDtoOutput saveUser(UserDtoRegistration userDtoRegistration) {
        User entityToSave = userMapper.userInputMapping(userDtoRegistration);
        userValidator.validateEntity(entityToSave);
        roleService.assignRoles(entityToSave);
        User user = save(entityToSave);
        return userMapper.registerOutputMapping(user);
    }

    @Override
    public UserDtoOutput getUserDto(String email) {
        User user = getUser(email);
        return this.userMapper.outputMapping(user);
    }

    @Override
    public PageDtoOutput<UserPageForAdminDtoOutput> getDtoForAdmin(Pageable pageable) {
        Page<User> page = get(pageable);
        return userMapper.outputPageForAdminMapping(page);
    }
}
