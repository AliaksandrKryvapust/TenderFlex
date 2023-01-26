package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenderMapper {
    private final CompanyDetailsMapper companyDetailsMapper;
    private final ContactPersonMapper contactPersonMapper;
    private final ContractMapper contractMapper;
    private final RejectDecisionMapper rejectDecisionMapper;
    private final UserMapper userMapper;

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
}
