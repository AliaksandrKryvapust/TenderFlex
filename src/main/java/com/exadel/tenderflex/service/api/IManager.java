package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IManager<TYPE, TYPE2, TYPE3> {
    TYPE saveDto(TYPE2 type);
    PageDtoOutput<TYPE3> getDto(Pageable pageable);
    TYPE getDto(UUID id);
}
