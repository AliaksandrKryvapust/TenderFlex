package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TenderValidatorTest {
    @InjectMocks
    private TenderValidator tenderValidator;

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
    final LocalDate submissionDeadline = LocalDate.parse("04/01/2023", df);
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final String description = "New contract";
    final Integer minPrice = 10500;
    final Integer maxPrice = 10800;

    @Test
    void validateNonEmptyId() {
        // preconditions
        final Tender tender = getPreparedTenderOutput();
        final String messageExpected = "Tender id should be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyContactPerson() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setContactPerson(null);

        final String messageExpected = "Contact person should not be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCompanyDetails() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setCompanyDetails(null);

        final String messageExpected = "Company details should not be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyUser() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setUser(null);

        final String messageExpected = "User should not be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyContract() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setContract(null);

        final String messageExpected = "Contract should not be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyRejectDecision() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setRejectDecision(null);

        final String messageExpected = "Reject decision should not be empty for tender: " + tender;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyOfficialName() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getCompanyDetails().setOfficialName(null);

        final String messageExpected = "Official name is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortOfficialName() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getCompanyDetails().setOfficialName("a");

        final String messageExpected = "Official name should contain from 2 to 50 letters for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyRegistrationNumber() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getCompanyDetails().setRegistrationNumber(null);

        final String messageExpected = "Registration number is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortRegistrationNumber() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getCompanyDetails().setRegistrationNumber("a");

        final String messageExpected = "Registration number should contain from 2 to 50 letters for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCountry() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getCompanyDetails().setCountry(null);

        final String messageExpected = "tender country is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyName() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setName(null);

        final String messageExpected = "Name is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortName() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setName("a");

        final String messageExpected = "Name should contain from 2 to 50 letters for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptySurname() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setSurname(null);

        final String messageExpected = "Surname is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortSurname() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setSurname("a");

        final String messageExpected = "Surname should contain from 2 to 50 letters for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyPhoneNumber() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setPhoneNumber(null);

        final String messageExpected = "Phone number is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateNegativePhoneNumber() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.getContactPerson().setPhoneNumber(-1L);

        final String messageExpected = "Phone number should be positive for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCpvCode() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setCpvCode(null);

        final String messageExpected = "Cpv Code is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyTenderType() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setTenderType(null);

        final String messageExpected = "tender type is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortDescription() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setDescription("a");

        final String messageExpected = "Description should contain from 2 to 250 letters for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyMinPrice() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setMinPrice(null);

        final String messageExpected = "Min price is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateNegativeMinPrice() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setMinPrice(-1);

        final String messageExpected = "Min price should be positive for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyMaxPrice() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setMaxPrice(null);

        final String messageExpected = "Max price is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateLowMaxPrice() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setMaxPrice(tender.getMinPrice());

        final String messageExpected = "Max price should be greater than min price for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyCurrency() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setCurrency(null);

        final String messageExpected = "currency is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyPublicationDate() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setPublication(null);

        final String messageExpected = "Publication Date is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateFuturePublicationDate() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        tender.setPublication(date);

        final String messageExpected = "Publication Date should refer to moment in the past for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }


    @Test
    void validateEmptyDeadline() {
        // preconditions
        final Tender tender = getPreparedTenderInput();
        tender.setSubmissionDeadline(null);

        final String messageExpected = "Deadline Date is not valid for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateWrongDeadline() {
        // preconditions
        final Tender tender = getPreparedTenderInput();

        final String messageExpected = "Deadline Date should be at least 1 day after publication for tender:" + tender;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> tenderValidator.validateEntity(tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void optimisticLockCheck() {
        // preconditions
        final Tender tender = getPreparedTenderOutput();
        final Long version = dtUpdate.toEpochMilli() - 1000;
        final String messageExpected = "tender table update failed, version does not match update denied";

        //test
        Exception actualException = assertThrows(OptimisticLockException.class, () ->
                tenderValidator.optimisticLockCheck(version, tender));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
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
                .submissionDeadline(submissionDeadline)
                .build();
    }

    Tender getPreparedTenderOutput() {
        return Tender.builder()
                .id(id)
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
                .submissionDeadline(submissionDeadline)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
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
}