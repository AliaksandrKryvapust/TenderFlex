package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.repository.entity.CompanyDetails;

public interface ICompanyDetailsValidator {
    void validateEntity(CompanyDetails companyDetails);
}
