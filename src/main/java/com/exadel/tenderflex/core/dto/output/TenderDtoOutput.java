package com.exadel.tenderflex.core.dto.output;

import com.exadel.tenderflex.repository.entity.enums.ELanguage;
import com.exadel.tenderflex.repository.entity.enums.EProcedure;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Data
public class TenderDtoOutput {
    private final @NotNull String id;
    private final @NotNull CompanyDetailsDtoOutput contractor;
    private final @NotNull ContactPersonDtoOutput contactPerson;
    private final @NotNull EProcedure procedure;
    private final @NotNull ELanguage language;
    private final @NotNull String cpvCode;
    private final @NotNull String tenderType;
    private final @NotNull String description;
    private final @NotNull Integer minPrice;
    private final @NotNull Integer maxPrice;
    private final @NotNull String currency;
    private final @NotNull LocalDate publication;
    private final @NotNull LocalDate submissionDeadline;
    private final @NotNull FilesDtoOutput files;
}
