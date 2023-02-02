package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OfferValidatorTest {
    @InjectMocks
    private OfferValidator offerValidator;
    @Mock
    private CompanyDetailsValidator companyDetailsValidator;
    @Mock
    private ContactPersonValidator contactPersonValidator;

    // preconditions
    final String username = "someone";
    final String password = "kdrL556D";
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "contractor@gmail.com";
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb250cmFjdG9yQGdtYWlsLmNvbSIsImlhdCI6MTY3NDU2Nzk1OCwiZXhwIjoxNjc0NTcxNTU4fQ.nSk39xKnJPOmuci9TLzIzZyya-sTIm9IZIgAINNFRCw";
    final String officialName = "TenderCompany";
    final String registrationNumber = "ULG BE 0325 777 171";
    final String country = "POLAND";
    final String name = "Marek";
    final String surname = "KOWALSKI";
    final Long phoneNumber = 48251173301L;
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final Integer maxPrice = 10800;

    @Test
    void validateNonEmptyId() {
        // preconditions
        final Offer offer = getPreparedOfferOutput();
        final String messageExpected = "Offer id should be empty for offer: " + offer;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyContactPerson() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setContactPerson(null);

        final String messageExpected = "Contact person should not be empty for offer: " + offer;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCompanyDetails() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setBidder(null);

        final String messageExpected = "Company details should not be empty for offer: " + offer;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyUser() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setUser(null);

        final String messageExpected = "User should not be empty for offer: " + offer;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyFile() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setPropositionFile(null);

        final String messageExpected = "Proposition file should not be empty for offer: " + offer;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyBidPrice() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setBidPrice(null);

        final String messageExpected = "Bid price is not valid for offer:" + offer;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateNegativeBidPrice() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setBidPrice(-1);

        final String messageExpected = "Bid should be positive for offer:" + offer;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCurrency() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setCurrency(null);

        final String messageExpected = "currency is not valid for offer:" + offer;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyOfferStatus() {
        // preconditions
        final Offer offer = getPreparedOfferInput();
        offer.setOfferStatusBidder(null);

        final String messageExpected = "offer status bidder is not valid for offer:" + offer;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> offerValidator.validateEntity(offer));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void optimisticLockCheck() {
        // preconditions
        final Offer offer = getPreparedOfferOutput();
        final Long version = dtUpdate.toEpochMilli() - 1000;
        final String messageExpected = "offer table update failed, version does not match update denied";

        //test
        Exception actualException = assertThrows(OptimisticLockException.class, () ->
                offerValidator.optimisticLockCheck(version, offer));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
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
                .build();
    }
}