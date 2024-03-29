package com.exadel.tenderflex.core.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@Builder
@Data
public class TenderDtoOutput {
    private final @NotNull String id;
    private final @NotNull CompanyDetailsDtoOutput contractor;
    private final @NotNull ContactPersonDtoOutput contactPerson;
    private final @NotNull ContractDtoOutput contract;
    private final @Nullable RejectDecisionDtoOutput rejectDecision;
    private final @NotNull String procedure;
    private final @NotNull String language;
    private final @NotNull String cpvCode;
    private final @NotNull String tenderType;
    private final @NotNull String description;
    private final @NotNull Integer minPrice;
    private final @NotNull Integer maxPrice;
    private final @NotNull String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate publication;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate submissionDeadline;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
