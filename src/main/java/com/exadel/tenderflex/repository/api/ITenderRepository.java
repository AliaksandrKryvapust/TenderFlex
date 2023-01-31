package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ITenderRepository extends JpaRepository<Tender, UUID> {
}
