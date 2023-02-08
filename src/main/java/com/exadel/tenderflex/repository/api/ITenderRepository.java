package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static com.exadel.tenderflex.core.Constants.TENDER_ENTITY_GRAPH;

public interface ITenderRepository extends JpaRepository<Tender, UUID> {
    @Query("select t from Tender t where t.user.email=:email")
    @Transactional(readOnly = true)
    Page<Tender> findAllForUser(@Param("email") String email, Pageable pageable);

    @Transactional(readOnly = true)
    @EntityGraph(value = TENDER_ENTITY_GRAPH)
    Set<Tender> findAllBySubmissionDeadlineBeforeAndTenderStatus(LocalDate currentDate, ETenderStatus tenderStatus);
}
