package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.RejectDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRejectDecisionRepository extends JpaRepository<RejectDecision, UUID> {
}
