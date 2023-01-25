package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.enums.ECountry;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@Jacksonized
public class CompanyDetailsDtoInput {
    @NotNull(message = "official name cannot be null")
    @Size(min = 2, max = 50, message = "official name should contain from 2 to 50 letters")
    private String officialName;
    @NotNull(message = "registration number cannot be null")
    @Size(min = 2, max = 50, message = "registration number should contain from 2 to 50 letters")
    private String registrationNumber;
    @NotNull(message = "country cannot be null")
    @IValidEnum(enumClass = ECountry.class, message = "country does not match")
    private String country;
    @Nullable
    @Size(min = 2, max = 50, message = "city/town should contain from 2 to 50 letters")
    private String town;
}
