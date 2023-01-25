package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.CompanyDetails;
import com.exadel.tenderflex.repository.entity.ContactPerson;
import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder
@Data
@Jacksonized
public class OfferDtoInput {
    @Valid
    @NotNull(message = "company details cannot be null")
    private CompanyDetails bidder;
    @Valid
    @NotNull(message = "contact person cannot be null")
    private ContactPerson contactPerson;
    @NotNull(message = "bid price cannot be null")
    @Positive(message = "bid price cannot be negative")
    private Integer bidPrice;
    @NotNull(message = "currency cannot be null")
    @IValidEnum(enumClass = ECurrency.class, message = "currency does not match")
    private String currency;
    @NotNull(message = "offer status cannot be null")
    @IValidEnum(enumClass = EOfferStatus.class, message = "offer status does not match")
    private String offerStatus;
}
