package com.exadel.tenderflex.core.dto.output.pages;

import com.exadel.tenderflex.core.dto.output.OfferDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class TenderPageDtoOutput {
    private final @NotNull String id;
    private final @NotNull UserLoginDtoOutput user;
    private final @NotNull String cpvCode;
    private final @NonNull String officialName;
    private final @NotNull String tenderStatus;
    private final @NotNull LocalDate submissionDeadline;
    private final @Nullable Set<OfferDtoOutput> offers;
    private final @Nullable Integer offersAmount;
}