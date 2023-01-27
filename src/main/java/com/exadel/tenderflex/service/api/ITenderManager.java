package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface ITenderManager {
    PageDtoOutput<TenderPageDtoOutput> getDto(Pageable pageable);

    TenderDtoOutput getDto(UUID id);

    TenderDtoOutput saveDto(String tender, Map<EFileType, MultipartFile> fileMap);

    TenderDtoOutput updateDto(String tender, Map<EFileType, MultipartFile> fileMap, UUID id, Long version);
}
