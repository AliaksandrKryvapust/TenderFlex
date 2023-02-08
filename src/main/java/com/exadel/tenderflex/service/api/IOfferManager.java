package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.output.OfferDtoOutput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface IOfferManager {
    PageDtoOutput<OfferPageForBidderDtoOutput> getDto(Pageable pageable);

    OfferDtoOutput getDto(UUID id);

    OfferDtoOutput saveDto(String tender, Map<EFileType, MultipartFile> fileMap);

    OfferDtoOutput updateDto(String offer, Map<EFileType, MultipartFile> fileMap, UUID id, Long version);
    OfferDtoOutput awardAction(ActionDto actionDto);
}
