package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRoleRepository extends JpaRepository<Role, UUID> {
}
