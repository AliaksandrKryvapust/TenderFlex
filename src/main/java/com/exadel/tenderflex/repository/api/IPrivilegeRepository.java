package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPrivilegeRepository extends JpaRepository<Privilege, UUID> {
}
