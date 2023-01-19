package com.exadel.tenderflex.service;

import com.exadel.tenderflex.repository.api.IRoleRepository;
import com.exadel.tenderflex.repository.api.IUserRepository;
import com.exadel.tenderflex.repository.entity.Privilege;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.api.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.UUID;

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
        this.setPrivileges(user);
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
        this.validate(user);
        User currentEntity = this.userRepository.findById(id).orElseThrow();
        this.optimisticLockCheck(version, currentEntity);
        this.updateEntityFields(user, currentEntity);
        return null;
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

    private void setPrivileges(User user) {
        user.getRoles().forEach((i) -> {
            List<Privilege> rolePrivilege = this.roleRepository.getRoleByRole(i.getRole()).orElseThrow().getPrivileges();
            i.setPrivileges(rolePrivilege);
        });
    }

    @Override
    public User getUser(String email) {
        return this.userRepository.findByEmail(email);
    }
}
