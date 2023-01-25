package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOfferRepository extends JpaRepository<Offer, UUID> {
}
