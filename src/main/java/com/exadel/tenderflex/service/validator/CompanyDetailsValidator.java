package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.CompanyDetails;
import com.exadel.tenderflex.service.validator.api.ICompanyDetailsValidator;
import org.springframework.stereotype.Component;

@Component
public class CompanyDetailsValidator implements ICompanyDetailsValidator {

    @Override
    public void validateEntity(CompanyDetails companyDetails) {
        checkOfficialName(companyDetails);
        checkRegistrationNumber(companyDetails);
        checkCountry(companyDetails);
        checkTown(companyDetails);
    }

    private void checkOfficialName(CompanyDetails companyDetails) {
        if (companyDetails.getOfficialName() == null || companyDetails.getOfficialName().isBlank()) {
            throw new IllegalArgumentException("Official name is not valid for company details:" + companyDetails);
        }
        char[] chars = companyDetails.getOfficialName().toCharArray();
        if (chars.length < 2 || chars.length > 50) {
            throw new IllegalArgumentException("Official name should contain from 2 to 50 letters for company details:" + companyDetails);
        }
    }

    private void checkRegistrationNumber(CompanyDetails companyDetails) {
        if (companyDetails.getRegistrationNumber() == null || companyDetails.getRegistrationNumber().isBlank()) {
            throw new IllegalArgumentException("Registration number is not valid for company details:" + companyDetails);
        }
        char[] chars = companyDetails.getRegistrationNumber().toCharArray();
        if (chars.length < 2 || chars.length > 50) {
            throw new IllegalArgumentException("Registration number should contain from 2 to 50 letters for company details:" + companyDetails);
        }
    }

    private void checkCountry(CompanyDetails companyDetails) {
        if (companyDetails.getCountry() == null) {
            throw new IllegalArgumentException("country is not valid for company details:" + companyDetails);
        }
    }

    private void checkTown(CompanyDetails companyDetails) {
        if (companyDetails.getTown() != null) {
            char[] chars = companyDetails.getTown().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Town should contain from 2 to 50 letters for company details:" + companyDetails);
            }
        }
    }
}
