package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ITenderRepository extends JpaRepository<Tender, UUID> {
    @Query("select t from Tender t where t.user.email=:email")
    Page<Tender> findAllForUser(@Param("email") String email, Pageable pageable);
}
