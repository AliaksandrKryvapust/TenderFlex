package com.exadel.tenderflex.service;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.EUserStatus;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.validator.api.IUserDetailsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final IUserDetailsValidator userDetailsValidator;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        userDetailsValidator.validate(email, user);
        boolean enabled = user.getStatus().equals(EUserStatus.ACTIVATED);
        boolean nonLocked = !user.getStatus().equals(EUserStatus.DEACTIVATED);
        Set<GrantedAuthority> authorityList = new HashSet<>();
        user.getRoles().forEach((i) -> {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + i.getRoleType().name()));
            for (Privilege privilege : i.getPrivileges()) {
                authorityList.add(new SimpleGrantedAuthority(privilege.getPrivilege().name()));
            }
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), enabled,
                true, true, nonLocked, authorityList);
    }

    public UserLoginDtoOutput login(UserDtoLogin userDtoLogin) {
        UserDetails userDetails = loadUserByUsername(userDtoLogin.getEmail());
        userDetailsValidator.validateLogin(userDtoLogin, userDetails);
        String token = jwtTokenUtil.generateToken(userDetails);
        return userMapper.loginOutputMapping(userDetails, token);
    }
}
