package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.api.IRejectDecisionRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.exadel.tenderflex.service.validator.api.IOfferValidator;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OfferTransactionalServiceTest {
    @InjectMocks
    private OfferTransactionalService offerTransactionalService;
    @Mock
    private IOfferRepository offerRepository;
    @Mock
    private ITenderRepository tenderRepository;
    @Mock
    private IRejectDecisionRepository rejectDecisionRepository;
    @Mock
    private IOfferValidator offerValidator;

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
        final Offer offerInput = getPreparedOfferInput();
        final Offer offerOutput = getPreparedOfferOutput();
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(offerRepository.save(offerInput)).thenReturn(offerOutput);
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderOutput));

        //test
        Offer actual = offerTransactionalService.saveTransactional(offerInput);

        // assert
        assertNotNull(actual);
        checkOfferOutputFields(actual);
    }

    @Test
    void awardPositiveAction() {
        // preconditions
        final ActionDto actionDto = getPreparedActionDto();
        final Offer offerOutput = getPreparedOfferOutput();
        final Tender tenderOutput = getPreparedTenderOutput();
        offerOutput.setTender(tenderOutput);
        tenderOutput.getOffers().stream().findFirst().orElseThrow().setOfferStatusContractor(EOfferStatus.OFFER_SELECTED);
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerOutput));
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);

        //test
        Offer actual = offerTransactionalService.awardTransactionalAction(actionDto);
        Mockito.verify(offerValidator, Mockito.times(1)).validateAwardDecision(actualOffer.capture());
        Mockito.verify(offerRepository, Mockito.times(1)).save(any(Offer.class));
        Mockito.verify(tenderRepository, Mockito.times(1)).save(any(Tender.class));
        Mockito.verify(rejectDecisionRepository, Mockito.times(1)).save(any(RejectDecision.class));

        // assert
        assertNotNull(actual);
        checkOfferOutputFieldsAward(actual);
    }

    @Test
    void awardNegativeAction() {
        // preconditions
        final ActionDto actionDto = ActionDto.builder()
                .tender(id)
                .offer(id)
                .award(false)
                .build();
        final Offer offerOutput = getPreparedOfferOutput();
        final Tender tenderOutput = getPreparedTenderOutput();
        offerOutput.setTender(tenderOutput);
        tenderOutput.getOffers().stream().findFirst().orElseThrow().setOfferStatusContractor(EOfferStatus.OFFER_SELECTED);
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerOutput));
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);

        //test
        Offer actual = offerTransactionalService.awardTransactionalAction(actionDto);
        Mockito.verify(offerValidator, Mockito.times(1)).validateAwardDecision(actualOffer.capture());
        Mockito.verify(offerRepository, Mockito.times(1)).save(any(Offer.class));
        Mockito.verify(tenderRepository, Mockito.times(1)).save(any(Tender.class));

        // assert
        assertNotNull(actual);
        checkOfferOutputFieldsNegativeAward(actual);
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
                .dtUpdate(dtUpdate)
                .build();
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

    File getPreparedFileOutput() {
        return File.builder()
                .id(id)
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .fileKey(token)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    Offer getPreparedOfferInput() {
        return Offer.builder()
                .user(getPreparedUserOutput())
                .bidder(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .propositionFile(getPreparedFileOutput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK)
                .offerStatusBidder(EOfferStatus.OFFER_SENT)
                .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                .tenderId(id)
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

    Tender getPreparedTenderOutput() {
        Set<Offer> offers = new HashSet<>();
        offers.add(getPreparedOfferOutput());
        return Tender.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .companyDetails(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .contract(getPreparedContractOutput())
                .rejectDecision(getPreparedRejectDecisionOutput())
                .offers(offers)
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
                .dtUpdate(dtUpdate)
                .build();
    }

    Contract getPreparedContractOutput() {
        return Contract.builder()
                .id(id)
                .contractDeadline(submissionDeadline)
                .files(Collections.singleton(getPreparedFileOutput()))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    RejectDecision getPreparedRejectDecisionOutput() {
        return RejectDecision.builder()
                .id(id)
                .file(getPreparedFileOutput())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    ActionDto getPreparedActionDto(){
        return ActionDto.builder()
                .tender(id)
                .offer(id)
                .award(true)
                .build();
    }

    private void checkOfferOutputFields(Offer actual) {
        assertNotNull(actual.getBidder());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getPropositionFile());
        assertEquals(id, actual.getId());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(EOfferStatus.OFFER_SENT, actual.getOfferStatusBidder());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getBidder().getOfficialName());
        assertEquals(registrationNumber, actual.getBidder().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getBidder().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getPropositionFile().getId());
        assertEquals(fileName, actual.getPropositionFile().getFileName());
        assertEquals(token, actual.getPropositionFile().getFileKey());
        assertEquals(contentType, actual.getPropositionFile().getContentType());
        assertEquals(url, actual.getPropositionFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getPropositionFile().getFileType());
        assertEquals(dtCreate, actual.getPropositionFile().getDtCreate());
        assertEquals(dtUpdate, actual.getPropositionFile().getDtUpdate());
    }

    private void checkOfferOutputFieldsAward(Offer actual) {
        assertNotNull(actual.getBidder());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getPropositionFile());
        assertEquals(id, actual.getId());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(EOfferStatus.CONTRACT_APPROVED_BY_BIDDER, actual.getOfferStatusBidder());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getBidder().getOfficialName());
        assertEquals(registrationNumber, actual.getBidder().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getBidder().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getPropositionFile().getId());
        assertEquals(fileName, actual.getPropositionFile().getFileName());
        assertEquals(token, actual.getPropositionFile().getFileKey());
        assertEquals(contentType, actual.getPropositionFile().getContentType());
        assertEquals(url, actual.getPropositionFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getPropositionFile().getFileType());
        assertEquals(dtCreate, actual.getPropositionFile().getDtCreate());
        assertEquals(dtUpdate, actual.getPropositionFile().getDtUpdate());
    }

    private void checkOfferOutputFieldsNegativeAward(Offer actual) {
        assertNotNull(actual.getBidder());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getPropositionFile());
        assertEquals(id, actual.getId());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(EOfferStatus.CONTRACT_DECLINED_BY_BIDDER, actual.getOfferStatusBidder());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getBidder().getOfficialName());
        assertEquals(registrationNumber, actual.getBidder().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getBidder().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getPropositionFile().getId());
        assertEquals(fileName, actual.getPropositionFile().getFileName());
        assertEquals(token, actual.getPropositionFile().getFileKey());
        assertEquals(contentType, actual.getPropositionFile().getContentType());
        assertEquals(url, actual.getPropositionFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getPropositionFile().getFileType());
        assertEquals(dtCreate, actual.getPropositionFile().getDtCreate());
        assertEquals(dtUpdate, actual.getPropositionFile().getDtUpdate());
    }
}