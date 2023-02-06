package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface IRoleRepository extends JpaRepository<Role, UUID> {
    @Transactional(readOnly = true)
    Optional<Role> getRoleByRoleType(EUserRole role);
}
