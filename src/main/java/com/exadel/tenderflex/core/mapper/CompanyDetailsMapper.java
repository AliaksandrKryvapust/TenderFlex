package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.CompanyDetailsDtoInput;
import com.exadel.tenderflex.core.dto.output.CompanyDetailsDtoOutput;
import com.exadel.tenderflex.repository.entity.CompanyDetails;
import com.exadel.tenderflex.repository.entity.enums.ECountry;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CompanyDetailsMapper {
    public CompanyDetails inputMapping(CompanyDetailsDtoInput dtoInput){
        return CompanyDetails.builder()
                .officialName(dtoInput.getOfficialName())
                .registrationNumber(dtoInput.getRegistrationNumber())
                .country(ECountry.valueOf(dtoInput.getCountry()))
                .town(dtoInput.getTown())
                .build();
    }

    public CompanyDetailsDtoOutput outputMapping(CompanyDetails companyDetails){
        return CompanyDetailsDtoOutput.builder()
                .officialName(companyDetails.getOfficialName())
                .registrationNumber(companyDetails.getRegistrationNumber())
                .country(companyDetails.getCountry().name())
                .town(companyDetails.getTown())
                .build();
    }
}
