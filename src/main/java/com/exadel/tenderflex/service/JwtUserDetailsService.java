package com.exadel.tenderflex.service;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.mapper.UserMapper;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.cache.CacheStorage;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
import com.exadel.tenderflex.service.validator.api.IUserDetailsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final CacheStorage<Object> tokenBlackList;
    private final IUserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final IUserDetailsValidator userDetailsValidator;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
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
        setLoginDate(userDetails);
        return userMapper.loginOutputMapping(userDetails, token);
    }

    public void logout(HttpServletRequest request) {
        String requestTokenHeader = request.getHeader(AUTHORIZATION);
        String jwtToken = requestTokenHeader.substring(7);
        this.tokenBlackList.add(jwtToken, new Object());
    }

    public boolean tokenIsInBlackList(String token) {
        return tokenBlackList.get(token) != null;
    }

    private void setLoginDate(UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(NoSuchElementException::new);
        LocalDate date = LocalDate.now(ZoneOffset.UTC);
        currentUser.setDtLogin(date);
        userRepository.save(currentUser);
    }
}
