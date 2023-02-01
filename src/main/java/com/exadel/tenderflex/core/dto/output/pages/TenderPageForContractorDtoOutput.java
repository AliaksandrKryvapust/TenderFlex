package com.exadel.tenderflex.core.dto.output.pages;

import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class TenderPageForContractorDtoOutput {
    private final @NotNull String id;
    private final @NotNull UserLoginDtoOutput user;
    private final @NotNull String cpvCode;
    private final @NonNull String officialName;
    private final @NotNull String tenderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate submissionDeadline;
    private final @Nullable Set<OfferPageForContractorDtoOutput> offers;
    private final @Nullable Integer offersAmount;
}
