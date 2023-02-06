package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForContractorDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForContractorDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface ITenderManager {
    PageDtoOutput<TenderPageForContractorDtoOutput> getDto(Pageable pageable);
    PageDtoOutput<OfferPageForContractorDtoOutput> getOfferForTender(UUID id, Pageable pageable);

    PageDtoOutput<OfferPageForContractorDtoOutput> getOfferForContractor(Pageable pageable);
    TenderDtoOutput getDto(UUID id);
    PageDtoOutput<TenderPageForBidderDtoOutput> getDtoAll(Pageable pageable);

    TenderDtoOutput saveDto(String tender, Map<EFileType, MultipartFile> fileMap);

    TenderDtoOutput updateDto(String tender, Map<EFileType, MultipartFile> fileMap, UUID id, Long version);

    TenderDtoOutput awardAction(ActionDto actionDto);
}
