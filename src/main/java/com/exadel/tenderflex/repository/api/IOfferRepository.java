package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface IOfferRepository extends JpaRepository<Offer, UUID> {
    @Transactional(readOnly = true)
    @Query("select o from Offer o where o.user.email=:email")
    Page<Offer> findAllForUser(@Param("email") String email, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select o from Offer o where o.tender.id=:id")
    Page<Offer> findAllForTender(@Param("id") UUID id, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select o from Offer o where o.tender.user.email=:email")
    Page<Offer> findAllForContractor(@Param("email") String email, Pageable pageable);

    @Transactional(readOnly = true)
    Set<Offer> findAllByContract_ContractDeadlineBeforeAndOfferStatusBidder(LocalDate currentDate, EOfferStatus offerStatus);
}
