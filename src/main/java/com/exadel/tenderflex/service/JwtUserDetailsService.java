package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.EUserStatus;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.aopalliance.aop.AspectException;
import org.springframework.aop.framework.AopContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        JwtUserDetailsService proxy = getProxy();
        User user = proxy.getUser(email);
        this.validate(email, user);
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

    @Transactional(readOnly = true)
    public User getUser(String email){
        return this.userRepository.findByEmail(email);
    }

    private void validate(String email, User user) {
        if (user == null) {
            throw new NoSuchElementException("There is no such user" + email);
        }
    }

    private JwtUserDetailsService getProxy() {
        try {
            return (JwtUserDetailsService) AopContext.currentProxy();
        } catch (AspectException e) {
            return this;
        }
    }
}
