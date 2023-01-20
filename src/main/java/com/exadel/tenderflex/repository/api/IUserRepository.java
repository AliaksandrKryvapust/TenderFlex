package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import static com.exadel.tenderflex.core.Constants.USER_ENTITY_GRAPH;

public interface IUserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(value = USER_ENTITY_GRAPH)
    User findByEmail(String email);
}
