package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.EUserStatus;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;

    public JwtUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = this.userRepository.findByEmail(email);
        this.validate(email, user);
        boolean enabled = user.getStatus().equals(EUserStatus.ACTIVATED);
        boolean nonLocked = !user.getStatus().equals(EUserStatus.DEACTIVATED);
        Set<GrantedAuthority> authorityList = new HashSet<>();
        user.getRoles().forEach((i) -> {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + i.getRole().name()));
            for (Privilege privilege : i.getPrivileges()) {
                authorityList.add(new SimpleGrantedAuthority(privilege.getPrivilege().name()));
            }
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), enabled,
                true, true, nonLocked, authorityList);
    }

    private void validate(String email, User user) {
        if (user == null) {
            throw new NoSuchElementException("There is no such user" + email);
        }
    }
}
