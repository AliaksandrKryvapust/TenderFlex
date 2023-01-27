package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenderMapper {
    private final CompanyDetailsMapper companyDetailsMapper;
    private final ContactPersonMapper contactPersonMapper;
    private final ContractMapper contractMapper;
    private final RejectDecisionMapper rejectDecisionMapper;
    private final UserMapper userMapper;
    private final OfferMapper offerMapper;
    private final ObjectMapper objectMapper;

    public TenderDtoInput extractJson(String tender) {
        try {
            return objectMapper.readValue(tender, TenderDtoInput.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Wrong json format for entity tender: " + tender);
        }
    }

    public Tender inputMapping(TenderDtoInput dtoInput, User user) {
        CompanyDetails companyDetails = companyDetailsMapper.inputMapping(dtoInput.getContractor());
        ContactPerson contactPerson = contactPersonMapper.inputMapping(dtoInput.getContactPerson());
        Contract contract = contractMapper.inputMapping(dtoInput.getFiles());
        RejectDecision rejectDecision = rejectDecisionMapper.inputMapping(dtoInput.getFiles());
        return Tender.builder()
                .user(user)
                .contactPerson(contactPerson)
                .companyDetails(companyDetails)
                .contract(contract)
                .rejectDecision(rejectDecision)
                .cpvCode(dtoInput.getCpvCode())
                .tenderType(ETenderType.valueOf(dtoInput.getTenderType()))
                .description(dtoInput.getDescription())
                .minPrice(dtoInput.getMinPrice())
                .maxPrice(dtoInput.getMaxPrice())
                .currency(ECurrency.valueOf(dtoInput.getCurrency()))
                .publication(dtoInput.getPublication())
                .submissionDeadline(dtoInput.getSubmissionDeadline())
                .tenderStatus(ETenderStatus.IN_PROGRESS).build();
    }

    public TenderDtoOutput outputMapping(Tender tender) {
        UserLoginDtoOutput user = userMapper.registerOutputMapping(tender.getUser());
        CompanyDetailsDtoOutput companyDetails = companyDetailsMapper.outputMapping(tender.getCompanyDetails());
        ContactPersonDtoOutput contactPerson = contactPersonMapper.outputMapping(tender.getContactPerson());
        ContractDtoOutput contract = contractMapper.outputMapping(tender.getContract());
        RejectDecisionDtoOutput rejectDecision = rejectDecisionMapper.outputMapping(tender.getRejectDecision());
        return TenderDtoOutput.builder()
                .id(tender.getId().toString())
                .user(user)
                .contractor(companyDetails)
                .contactPerson(contactPerson)
                .contract(contract)
                .rejectDecision(rejectDecision)
                .procedure(EProcedure.OPEN_PROCEDURE.name())
                .language(ELanguage.ENGLISH.name())
                .cpvCode(tender.getCpvCode())
                .tenderType(tender.getTenderType().name())
                .description(tender.getDescription())
                .minPrice(tender.getMinPrice())
                .maxPrice(tender.getMaxPrice())
                .currency(tender.getCurrency().name())
                .publication(tender.getPublication())
                .submissionDeadline(tender.getSubmissionDeadline())
                .tenderStatus(tender.getTenderStatus().name())
                .dtCreate(tender.getDtCreate())
                .dtUpdate(tender.getDtUpdate()).build();
    }

    public TenderPageDtoOutput tenderPageOutputMapping(Tender tender){
        UserLoginDtoOutput user = userMapper.registerOutputMapping(tender.getUser());
        if (!tender.getTenderStatus().equals(ETenderStatus.CLOSED)){
            if (tender.getOffers() != null) {
                Set<OfferDtoOutput> offers = offerMapper.listOutputMapping(tender.getOffers());
                return TenderPageDtoOutput.builder()
                        .id(tender.getId().toString())
                        .user(user)
                        .cpvCode(tender.getCpvCode())
                        .officialName(tender.getCompanyDetails().getOfficialName())
                        .tenderStatus(tender.getTenderStatus().name())
                        .submissionDeadline(tender.getSubmissionDeadline())
                        .offers(offers)
                        .offersAmount(offers.size()).build();
            } else {
                return TenderPageDtoOutput.builder()
                        .id(tender.getId().toString())
                        .user(user)
                        .cpvCode(tender.getCpvCode())
                        .officialName(tender.getCompanyDetails().getOfficialName())
                        .tenderStatus(tender.getTenderStatus().name())
                        .submissionDeadline(tender.getSubmissionDeadline())
                        .offersAmount(0).build();
            }
        } else {
            return TenderPageDtoOutput.builder()
                    .id(tender.getId().toString())
                    .user(user)
                    .cpvCode(tender.getCpvCode())
                    .officialName(tender.getCompanyDetails().getOfficialName())
                    .tenderStatus(tender.getTenderStatus().name())
                    .submissionDeadline(tender.getSubmissionDeadline()).build();
        }
    }

    public PageDtoOutput<TenderPageDtoOutput> outputPageMapping(Page<Tender> record){
        Set<TenderPageDtoOutput> outputs = record.getContent().stream().map(this::tenderPageOutputMapping).collect(Collectors.toSet());
        return PageDtoOutput.<TenderPageDtoOutput>builder()
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

    public void updateEntityFields(Tender tender, Tender currentEntity){
        currentEntity.getContactPerson().setName(tender.getContactPerson().getName());
        currentEntity.getContactPerson().setSurname(tender.getContactPerson().getSurname());
        currentEntity.getContactPerson().setPhoneNumber(tender.getContactPerson().getPhoneNumber());
        currentEntity.getCompanyDetails().setOfficialName(tender.getCompanyDetails().getOfficialName());
        currentEntity.getCompanyDetails().setRegistrationNumber(tender.getCompanyDetails().getRegistrationNumber());
        currentEntity.getCompanyDetails().setCountry(tender.getCompanyDetails().getCountry());
        currentEntity.getCompanyDetails().setTown(tender.getCompanyDetails().getTown());
        currentEntity.getContract().setContractDeadline(tender.getContract().getContractDeadline());
        currentEntity.getContract().setContractFile(tender.getContract().getContractFile());
        currentEntity.getContract().setAwardDecisionFile(tender.getContract().getAwardDecisionFile());
        if (tender.getRejectDecision()!=null){
            if (currentEntity.getRejectDecision()==null){
                currentEntity.setRejectDecision(tender.getRejectDecision());
            } else {
                currentEntity.getRejectDecision().setRejectDecisionFile(tender.getRejectDecision().getRejectDecisionFile());
            }
        }
        currentEntity.setCpvCode(tender.getCpvCode());
        currentEntity.setTenderType(tender.getTenderType());
        currentEntity.setDescription(tender.getDescription());
        currentEntity.setMinPrice(tender.getMinPrice());
        currentEntity.setMaxPrice(tender.getMaxPrice());
        currentEntity.setCurrency(tender.getCurrency());
        currentEntity.setSubmissionDeadline(tender.getSubmissionDeadline());
    }
}
