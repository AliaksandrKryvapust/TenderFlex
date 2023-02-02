package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IOfferRepository extends JpaRepository<Offer, UUID> {
    @Query("select o from Offer o where o.user.email=:email")
    Page<Offer> findAllForUser(@Param("email") String email, Pageable pageable);

    @Query("select o from Offer o where o.tender.id=:id")
    Page<Offer> findAllForTender(@Param("id") String id, Pageable pageable);
}
