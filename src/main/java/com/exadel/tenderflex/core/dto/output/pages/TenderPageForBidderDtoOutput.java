package com.exadel.tenderflex.core.dto.output.pages;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Data
public class TenderPageForBidderDtoOutput {
    private final @NotNull String id;
    private final @NotNull String cpvCode;
    private final @NonNull String officialName;
    private final @NotNull String tenderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate submissionDeadline;
    private final @NotNull String offerStatus;
}
