package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IContractRepository extends JpaRepository<Contract, UUID> {
}
