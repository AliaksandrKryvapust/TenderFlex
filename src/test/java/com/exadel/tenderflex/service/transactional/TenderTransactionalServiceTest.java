package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.repository.api.IContractRepository;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.api.IRejectDecisionRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.exadel.tenderflex.service.validator.api.IContractValidator;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TenderTransactionalServiceTest {
    @InjectMocks
    private TenderTransactionalService tenderTransactionalService;
    @Mock
    private ITenderRepository tenderRepository;
    @Mock
    private IOfferRepository offerRepository;
    @Mock
    private IContractRepository contractRepository;
    @Mock
    private IRejectDecisionRepository rejectDecisionRepository;
    @Mock
    private ITenderValidator tenderValidator;
    @Mock
    private IContractValidator contractValidator;

    // preconditions
    final String username = "someone";
    final String password = "kdrL556D";
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "contractor@gmail.com";
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb250cmFjdG9yQGdtYWlsLmNvbSIsImlhdCI6MTY3NDU2Nzk1OCwiZXhwIjoxNjc0NTcxNTU4fQ.nSk39xKnJPOmuci9TLzIzZyya-sTIm9IZIgAINNFRCw";
    final String cpvCode = "45262420-1 Structural steel erection work for structures";
    final String officialName = "TenderCompany";
    final String registrationNumber = "ULG BE 0325 777 171";
    final String country = "POLAND";
    final String name = "Marek";
    final String surname = "KOWALSKI";
    final Long phoneNumber = 48251173301L;
    final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    final LocalDate submissionDeadline = LocalDate.parse("04/04/2023", df);
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final String description = "New contract";
    final Integer minPrice = 10500;
    final Integer maxPrice = 10800;

    @Test
    void saveTransactional() {
        // preconditions
        final Tender tenderInput = getPreparedTenderInput();
        final Tender tenderOutput = getPreparedTenderOutput();
        final Contract contractOutput = getPreparedContractOutput();
        final RejectDecision rejectDecisionOutput = getPreparedRejectDecisionOutput();
        Mockito.when(tenderRepository.save(tenderInput)).thenReturn(tenderOutput);
        Mockito.when(contractRepository.save(contractOutput)).thenReturn(contractOutput);
        Mockito.when(rejectDecisionRepository.save(rejectDecisionOutput)).thenReturn(rejectDecisionOutput);

        //test
        Tender actual = tenderTransactionalService.saveTransactional(tenderInput);

        // assert
        assertNotNull(actual);
        checkUserOutputFields(actual);
    }

    @Test
    void awardTransactionalAction() {
        // preconditions
        final ActionDto actionDto = getPreparedActionDto();
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderOutput));
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);
        ArgumentCaptor<Tender> actualTender = ArgumentCaptor.forClass(Tender.class);

        //test
        Tender actual = tenderTransactionalService.awardTransactionalAction(actionDto);
        Mockito.verify(tenderValidator, Mockito.times(1)).validateAwardCondition(actualOffer.capture(),
                actualTender.capture());
        Mockito.verify(offerRepository, Mockito.times(1)).save(any(Offer.class));
        Mockito.verify(contractValidator, Mockito.times(1)).validateEntity(any(Contract.class));
        Mockito.verify(contractRepository, Mockito.times(1)).save(any(Contract.class));

        // assert
        assertNotNull(actual);
        checkUserOutputFields(actual);
    }

    Tender getPreparedTenderInput() {
        return Tender.builder()
                .user(getPreparedUserOutput())
                .companyDetails(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .contract(getPreparedContractOutput())
                .rejectDecision(getPreparedRejectDecisionOutput())
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY)
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK)
                .tenderStatus(ETenderStatus.IN_PROGRESS)
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline).build();
    }

    Tender getPreparedTenderOutput() {
        return Tender.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .companyDetails(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .contract(getPreparedContractOutput())
                .rejectDecision(getPreparedRejectDecisionOutput())
                .offers(Collections.singleton(getPreparedOfferOutput()))
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY)
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK)
                .tenderStatus(ETenderStatus.IN_PROGRESS)
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    User getPreparedUserOutput() {
        final Privilege privilege = Privilege.builder()
                .id(id)
                .privilege(ERolePrivilege.CAN_READ_OFFER)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        final Role role = Role.builder()
                .id(id)
                .roleType(EUserRole.CONTRACTOR)
                .privileges(new HashSet<>(Collections.singleton(privilege)))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    CompanyDetails getPreparedCompanyDetails() {
        return CompanyDetails.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(ECountry.valueOf(country))
                .build();
    }

    ContactPerson getPreparedContactPerson() {
        return ContactPerson.builder()
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
                .build();
    }

    File getPreparedFileInput() {
        return File.builder()
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .url(url).build();
    }

    File getPreparedFileOutput() {
        return File.builder()
                .id(id)
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .fileKey(token)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    Contract getPreparedContractOutput() {
        return Contract.builder()
                .id(id)
                .contractDeadline(submissionDeadline)
                .files(Collections.singleton(getPreparedFileOutput()))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    RejectDecision getPreparedRejectDecisionOutput() {
        return RejectDecision.builder()
                .id(id)
                .file(getPreparedFileOutput())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    ActionDto getPreparedActionDto(){
        return ActionDto.builder()
                .tender(id)
                .offer(id)
                .award(true)
                .build();
    }

    Offer getPreparedOfferOutput() {
        return Offer.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .bidder(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .propositionFile(getPreparedFileOutput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK)
                .offerStatusBidder(EOfferStatus.OFFER_SENT)
                .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .tenderId(id)
                .build();
    }

    private void checkUserOutputFields(Tender actual) {
        assertNotNull(actual.getCompanyDetails());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getContract());
        assertNotNull(actual.getRejectDecision());
        assertEquals(id, actual.getId());
        assertEquals(cpvCode, actual.getCpvCode());
        assertEquals(ETenderType.SUPPLY, actual.getTenderType());
        assertEquals(description, actual.getDescription());
        assertEquals(minPrice, actual.getMinPrice());
        assertEquals(maxPrice, actual.getMaxPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(ETenderStatus.IN_PROGRESS, actual.getTenderStatus());
        assertEquals(submissionDeadline, actual.getPublication());
        assertEquals(submissionDeadline, actual.getSubmissionDeadline());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getCompanyDetails().getOfficialName());
        assertEquals(registrationNumber, actual.getCompanyDetails().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getCompanyDetails().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getContract().getId());
        assertEquals(submissionDeadline, actual.getContract().getContractDeadline());
        assertEquals(dtCreate, actual.getContract().getDtCreate());
        assertEquals(dtUpdate, actual.getContract().getDtUpdate());
        for (File file : actual.getContract().getFiles()) {
            assertEquals(id, file.getId());
            assertEquals(fileName, file.getFileName());
            assertEquals(token, file.getFileKey());
            assertEquals(contentType, file.getContentType());
            assertEquals(url, file.getUrl());
            assertEquals(EFileType.AWARD_DECISION, file.getFileType());
            assertEquals(dtCreate, file.getDtCreate());
            assertEquals(dtUpdate, file.getDtUpdate());
        }
        assertEquals(id, actual.getRejectDecision().getId());
        assertEquals(dtCreate, actual.getRejectDecision().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getDtUpdate());
        assertEquals(id, actual.getRejectDecision().getFile().getId());
        assertEquals(fileName, actual.getRejectDecision().getFile().getFileName());
        assertEquals(token, actual.getRejectDecision().getFile().getFileKey());
        assertEquals(contentType, actual.getRejectDecision().getFile().getContentType());
        assertEquals(url, actual.getRejectDecision().getFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getRejectDecision().getFile().getFileType());
        assertEquals(dtCreate, actual.getRejectDecision().getFile().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getFile().getDtUpdate());
    }
}