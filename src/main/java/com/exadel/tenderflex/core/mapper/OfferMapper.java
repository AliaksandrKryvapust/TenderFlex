package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.input.OfferDtoInput;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForContractorDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class OfferMapper {
    private final UserMapper userMapper;
    private final CompanyDetailsMapper companyDetailsMapper;
    private final ContactPersonMapper contactPersonMapper;
    private final FileMapper fileMapper;
    private final ObjectMapper objectMapper;

    public OfferDtoInput extractJson(String offer) {
        try {
            return objectMapper.readValue(offer, OfferDtoInput.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Wrong json format for entity offer: " + offer);
        }
    }

    public Offer inputMapping(OfferDtoInput dtoInput, User user, Map<EFileType, MultipartFile> mapInput,
                              Map<EFileType, AwsS3FileDto> urls) {
        CompanyDetails companyDetails = companyDetailsMapper.inputMapping(dtoInput.getCompanyDetails());
        ContactPerson contactPerson = contactPersonMapper.inputMapping(dtoInput.getContactPerson());
        File propositionFile = fileMapper.inputPropositionMapping(mapInput, urls);
        if (dtoInput.getTenderId() != null) {
            return Offer.builder()
                    .user(user)
                    .contactPerson(contactPerson)
                    .bidder(companyDetails)
                    .propositionFile(propositionFile)
                    .bidPrice(dtoInput.getBidPrice())
                    .currency(ECurrency.valueOf(dtoInput.getCurrency()))
                    .offerStatusBidder(EOfferStatus.OFFER_SENT)
                    .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                    .tenderId(UUID.fromString(dtoInput.getTenderId()))
                    .build();
        } else {
            return Offer.builder()
                    .user(user)
                    .contactPerson(contactPerson)
                    .bidder(companyDetails)
                    .propositionFile(propositionFile)
                    .bidPrice(dtoInput.getBidPrice())
                    .currency(ECurrency.valueOf(dtoInput.getCurrency()))
                    .offerStatusBidder(EOfferStatus.OFFER_HAS_NOT_SEND)
                    .build();
        }
    }

    public OfferDtoOutput outputMapping(Offer offer) {
        UserLoginDtoOutput user = userMapper.registerOutputMapping(offer.getUser());
        CompanyDetailsDtoOutput companyDetails = companyDetailsMapper.outputMapping(offer.getBidder());
        ContactPersonDtoOutput contactPerson = contactPersonMapper.outputMapping(offer.getContactPerson());
        FileDtoOutput propositionFile = fileMapper.outputMapping(offer.getPropositionFile());
        return OfferDtoOutput.builder()
                .id(offer.getId().toString())
                .user(user)
                .bidder(companyDetails)
                .contactPerson(contactPerson)
                .propositionFile(propositionFile)
                .bidPrice(offer.getBidPrice())
                .currency(offer.getCurrency().name())
                .offerStatus(offer.getOfferStatusBidder().name())
                .dtCreate(offer.getDtCreate())
                .dtUpdate(offer.getDtUpdate())
                .build();
    }

    public OfferPageForBidderDtoOutput offerPageForBidderOutputMapping(Offer offer) {
        UserLoginDtoOutput user = userMapper.registerOutputMapping(offer.getUser());
        return OfferPageForBidderDtoOutput.builder()
                .id(offer.getId().toString())
                .user(user)
                .officialName(offer.getBidder().getOfficialName())
                .fieldFromTenderCpvCode(offer.getTender().getCpvCode().substring(offer.getTender().getCpvCode().indexOf("\n" + 1)))
                .bidPrice(offer.getBidPrice())
                .country(offer.getBidder().getCountry().name())
                .dtCreate(offer.getDtCreate().atZone(ZoneOffset.UTC).toLocalDate())
                .offerStatus(offer.getOfferStatusBidder().name())
                .build();
    }

    public PageDtoOutput<OfferPageForBidderDtoOutput> outputBidderPageMapping(Page<Offer> record) {
        Set<OfferPageForBidderDtoOutput> outputs = record.getContent().stream().map(this::offerPageForBidderOutputMapping)
                .collect(Collectors.toSet());
        return PageDtoOutput.<OfferPageForBidderDtoOutput>builder()
                .number(record.getNumber() + 1)
                .size(record.getSize())
                .totalPages(record.getTotalPages())
                .totalElements(record.getTotalElements())
                .first(record.isFirst())
                .numberOfElements(record.getNumberOfElements())
                .last(record.isLast())
                .content(outputs)
                .build();
    }

    public OfferPageForContractorDtoOutput offerForContractorOutputMapping(Offer offer) {
        return OfferPageForContractorDtoOutput.builder()
                .id(offer.getId().toString())
                .tenderId(offer.getTenderId().toString())
                .officialName(offer.getBidder().getOfficialName())
                .fieldFromTenderCpvCode(offer.getTender().getCpvCode().substring(offer.getTender().getCpvCode().indexOf("\n")+ 1))
                .bidPrice(offer.getBidPrice())
                .country(offer.getBidder().getCountry().name())
                .dtCreate(offer.getDtCreate().atZone(ZoneOffset.UTC).toLocalDate())
                .offerStatus(offer.getOfferStatusBidder().name())
                .build();
    }

    public PageDtoOutput<OfferPageForContractorDtoOutput> outputContractorPageMapping(Page<Offer> record) {
        Set<OfferPageForContractorDtoOutput> outputs = record.getContent().stream().map(this::offerForContractorOutputMapping)
                .collect(Collectors.toSet());
        return PageDtoOutput.<OfferPageForContractorDtoOutput>builder()
                .number(record.getNumber() + 1)
                .size(record.getSize())
                .totalPages(record.getTotalPages())
                .totalElements(record.getTotalElements())
                .first(record.isFirst())
                .numberOfElements(record.getNumberOfElements())
                .last(record.isLast())
                .content(outputs)
                .build();
    }

    public void updateEntityFields(Offer offer, Offer currentEntity) {
        currentEntity.getContactPerson().setName(offer.getContactPerson().getName());
        currentEntity.getContactPerson().setSurname(offer.getContactPerson().getSurname());
        currentEntity.getContactPerson().setPhoneNumber(offer.getContactPerson().getPhoneNumber());
        currentEntity.getBidder().setOfficialName(offer.getBidder().getOfficialName());
        currentEntity.getBidder().setRegistrationNumber(offer.getBidder().getRegistrationNumber());
        currentEntity.getBidder().setCountry(offer.getBidder().getCountry());
        currentEntity.getBidder().setTown(offer.getBidder().getTown());
        currentEntity.setPropositionFile(offer.getPropositionFile());
        currentEntity.setBidPrice(offer.getBidPrice());
        currentEntity.setCurrency(offer.getCurrency());
        currentEntity.setOfferStatusBidder(offer.getOfferStatusBidder());
    }
}
