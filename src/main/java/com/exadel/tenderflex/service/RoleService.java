package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IRoleRepository;
import com.exadel.tenderflex.repository.entity.Role;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.api.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final IRoleRepository roleRepository;

    @Override
    public void setRoles(User user) {
        Set<Role> roles = new HashSet<>();
        user.getRoles().forEach((i) -> {
            Role userRole = this.roleRepository.getRoleByRoleType(i.getRoleType()).orElseThrow(NoSuchElementException::new);
            roles.add(userRole);
        });
        user.setRoles(roles);
    }
}
