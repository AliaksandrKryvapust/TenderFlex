package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Builder
@Data
public class OfferDtoOutput {
    private final @NotNull String id;
    private final @NotNull CompanyDetailsDtoOutput bidder;
    private final @NotNull ContactPersonDtoOutput contactPerson;
    private final @NotNull Integer bidPrice;
    private final @NotNull String currency;
    private final @NotNull String offerStatus;
    private final @NotNull FileDtoOutput propositionFile;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
