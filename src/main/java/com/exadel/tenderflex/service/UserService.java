package com.exadel.tenderflex.service;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.api.IRoleService;
import com.exadel.tenderflex.service.api.IUserManager;
import com.exadel.tenderflex.service.api.IUserService;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import lombok.RequiredArgsConstructor;
import org.aopalliance.aop.AspectException;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, IUserManager {
    private final IUserRepository userRepository;
    private final IRoleService roleService;
    private final IUserValidator userValidator;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public User save(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Page<User> get(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public User get(UUID id) {
        return this.userRepository.findById(id).orElseThrow();
    }

    @Override
    public User update(User user, UUID id, Long version) {
        User currentEntity = this.userRepository.findById(id).orElseThrow();
        this.userValidator.optimisticLockCheck(version, currentEntity);
        this.userMapper.updateEntityFields(user, currentEntity);
        UserService proxy = getProxy();
        return proxy.save(currentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public UserDtoOutput saveDto(UserDtoInput userDtoInput) {
        User entityToSave = userMapper.inputMapping(userDtoInput);
        this.userValidator.validateEntity(entityToSave);
        this.roleService.setRoles(entityToSave);
        UserService proxy = getProxy();
        User user = proxy.save(entityToSave);
        return userMapper.outputMapping(user);
    }

    @Override
    public PageDtoOutput getDto(Pageable pageable) {
        return userMapper.outputPageMapping(this.get(pageable));
    }

    @Override
    public UserDtoOutput getDto(UUID id) {
        User menu = this.get(id);
        return userMapper.outputMapping(menu);
    }

    @Override
    public UserDtoOutput updateDto(UserDtoInput dtoInput, UUID id, Long version) {
        User entityToSave = userMapper.inputMapping(dtoInput);
        this.userValidator.validateEntity(entityToSave);
        User user = this.update(entityToSave, id, version);
        return userMapper.outputMapping(user);
    }

    @Override
    public UserLoginDtoOutput login(UserDtoLogin userDtoLogin) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userDtoLogin.getEmail());
        if (!encoder.matches(userDtoLogin.getPassword(), userDetails.getPassword()) || !userDetails.isEnabled()) {
            throw new BadCredentialsException("User login or password is incorrect or user is not activated");
        }
        String token = jwtTokenUtil.generateToken(userDetails);
        return this.userMapper.loginOutputMapping(userDetails, token);
    }

    @Override
    public UserLoginDtoOutput saveUser(UserDtoRegistration userDtoRegistration) {
        User entityToSave = userMapper.userInputMapping(userDtoRegistration);
        this.userValidator.validateEntity(entityToSave);
        this.roleService.setRoles(entityToSave);
        UserService proxy = getProxy();
        User user = proxy.save(entityToSave);
        return userMapper.registerOutputMapping(user);
    }

    @Override
    public UserDtoOutput getUserDto(String email) {
        UserService proxy = getProxy();
        User user = proxy.getUser(email);
        return this.userMapper.outputMapping(user);
    }

    private UserService getProxy() {
        try {
            return (UserService) AopContext.currentProxy();
        } catch (AspectException e) {
            return this;
        }
    }
}
