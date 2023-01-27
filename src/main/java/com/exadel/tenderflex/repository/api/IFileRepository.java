package com.exadel.tenderflex.repository.api;

import com.exadel.tenderflex.repository.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IFileRepository extends JpaRepository<File, UUID> {
}
