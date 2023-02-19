package com.exadel.tenderflex.core.dto.output.pages;

import com.exadel.tenderflex.core.dto.output.UserRegistrationDtoOutput;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Data
public class OfferPageForBidderDtoOutput {
    private final @NotNull String id;
    private final @NotNull UserRegistrationDtoOutput user;
    private final @NonNull String officialName;
    private final @NotNull String fieldFromTenderCpvCode;
    private final @NotNull Integer bidPrice;
    private final @NotNull String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate dtCreate;
    private final @NotNull Boolean active;
}
