package com.exadel.tenderflex.manager;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.manager.api.IUserManager;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.IUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserManager implements IUserManager {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final IUserService userService;
    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder encoder;

    public UserManager(JwtUserDetailsService jwtUserDetailsService, IUserService userService,
                       UserMapper userMapper, JwtTokenUtil jwtTokenUtil, PasswordEncoder encoder) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.encoder = encoder;
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
        User user = this.userService.save(userMapper.userInputMapping(userDtoRegistration));
        return userMapper.registerOutputMapping(user);
    }

    @Override
    public UserDtoOutput getUser(String email) {
        User user = this.userService.getUser(email);
        return this.userMapper.outputMapping(user);
    }

    @Override
    public UserDtoOutput save(UserDtoInput userDtoInput) {
        User user = this.userService.save(userMapper.inputMapping(userDtoInput));
        return userMapper.outputMapping(user);
    }

    @Override
    public PageDtoOutput get(Pageable pageable) {
        return userMapper.outputPageMapping(this.userService.get(pageable));
    }

    @Override
    public UserDtoOutput get(UUID id) {
        User menu = this.userService.get(id);
        return userMapper.outputMapping(menu);
    }

    @Override
    public UserDtoOutput update(UserDtoInput dtoInput, UUID id, Long version) {
        User user = this.userService.update(userMapper.inputMapping(dtoInput), id, version);
        return userMapper.outputMapping(user);
    }
}
