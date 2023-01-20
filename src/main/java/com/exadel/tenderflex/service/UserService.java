package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IRoleRepository;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.Role;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.api.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.*;

@Service
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    public UserService(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User save(User user) {
        this.validate(user);
        this.setRoles(user);
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
    @Transactional
    public User update(User user, UUID id, Long version) {
        this.validate(user);
        User currentEntity = this.userRepository.findById(id).orElseThrow();
        this.optimisticLockCheck(version, currentEntity);
        this.updateEntityFields(user, currentEntity);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(String email) {
        return this.userRepository.findByEmail(email);
    }

    private void validate(User user) {
        if (user.getId() != null || user.getDtUpdate() != null) {
            throw new IllegalStateException("User id should be empty");
        }
    }

    private void optimisticLockCheck(Long version, User currentEntity) {
        Long currentVersion = currentEntity.getDtUpdate().toEpochMilli();
        if (!currentVersion.equals(version)) {
            throw new OptimisticLockException("user table update failed, version does not match update denied");
        }
    }

    private void updateEntityFields(User user, User currentEntity) {
        currentEntity.setUsername(user.getUsername());
        currentEntity.setPassword(user.getPassword());
        currentEntity.setEmail(user.getEmail());
        currentEntity.setStatus(user.getStatus());
    }

    private void setRoles(User user) {
        List<Role> roles = new ArrayList<>();
        user.getRoles().forEach((i) -> {
            Role userRole = this.roleRepository.getRoleByRole(i.getRole()).orElseThrow();
            roles.add(userRole);
        });
        user.setRoles(roles);
    }
}
