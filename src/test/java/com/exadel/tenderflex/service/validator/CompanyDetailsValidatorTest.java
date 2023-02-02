package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.CompanyDetails;
import com.exadel.tenderflex.repository.entity.enums.ECountry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CompanyDetailsValidatorTest {
    @InjectMocks
    private CompanyDetailsValidator companyDetailsValidator;
    // preconditions
    final String officialName = "TenderCompany";
    final String registrationNumber = "ULG BE 0325 777 171";
    final String country = "POLAND";


    @Test
    void validateEmptyOfficialName() {
        // preconditions
        final CompanyDetails companyDetails = getPreparedCompanyDetails();
        companyDetails.setOfficialName(null);

        final String messageExpected = "Official name is not valid for company details:" + companyDetails;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> companyDetailsValidator.validateEntity(companyDetails));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortOfficialName() {
        // preconditions
        final CompanyDetails companyDetails = getPreparedCompanyDetails();
        companyDetails.setOfficialName("a");

        final String messageExpected = "Official name should contain from 2 to 50 letters for company details:" + companyDetails;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> companyDetailsValidator.validateEntity(companyDetails));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyRegistrationNumber() {
        // preconditions
        final CompanyDetails companyDetails = getPreparedCompanyDetails();
        companyDetails.setRegistrationNumber(null);

        final String messageExpected = "Registration number is not valid for company details:" + companyDetails;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> companyDetailsValidator.validateEntity(companyDetails));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortRegistrationNumber() {
        // preconditions
        final CompanyDetails companyDetails = getPreparedCompanyDetails();
        companyDetails.setRegistrationNumber("a");

        final String messageExpected = "Registration number should contain from 2 to 50 letters for company details:" + companyDetails;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> companyDetailsValidator.validateEntity(companyDetails));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }


    @Test
    void validateEmptyCountry() {
        // preconditions
        final CompanyDetails companyDetails = getPreparedCompanyDetails();
        companyDetails.setCountry(null);

        final String messageExpected = "country is not valid for company details:" + companyDetails;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> companyDetailsValidator.validateEntity(companyDetails));

        // assert
        assertEquals(messageExpected, actualException.getMessage());
    }

    CompanyDetails getPreparedCompanyDetails() {
        return CompanyDetails.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(ECountry.valueOf(country))
                .build();
    }
}