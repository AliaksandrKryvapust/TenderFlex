package com.exadel.tenderflex.core.dto.output.pages;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Data
public class OfferPageForContractorDtoOutput {
    private final @NotNull String id;
    private final @NotNull String tenderId;
    private final @NonNull String officialName;
    private final @NotNull String fieldFromTenderCpvCode;
    private final @NotNull Integer bidPrice;
    private final @NotNull String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate dtCreate;
    private final @NotNull String offerStatus;
}
