package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.exadel.tenderflex.core.Constants.USER_ENTITY_GRAPH;

public interface IUserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(value = USER_ENTITY_GRAPH)
    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);

    @Override
    @Transactional
    <S extends User> @NonNull S save(@NonNull S entity);
}
