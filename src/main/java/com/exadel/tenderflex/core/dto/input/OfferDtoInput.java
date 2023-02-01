package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder
@Data
@Jacksonized
public class OfferDtoInput {
    @Valid
    @NotNull(message = "company details cannot be null")
    private final CompanyDetailsDtoInput companyDetails;
    @Valid
    @NotNull(message = "contact person cannot be null")
    private final ContactPersonDtoInput contactPerson;
    @NotNull(message = "bid price cannot be null")
    @Positive(message = "bid price cannot be negative")
    private final Integer bidPrice;
    @NotNull(message = "currency cannot be null")
    @IValidEnum(enumClass = ECurrency.class, message = "currency does not match")
    private final String currency;
    @NotBlank(message = "tender is not selected")
    private final String tenderId;
}
