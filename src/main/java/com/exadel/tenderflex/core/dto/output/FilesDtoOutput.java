package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class FilesDtoOutput {
    private final @NotNull LocalDate contractDeadline;
    private final @NotNull UUID contractFile;
    private final @NotNull UUID awardDecisionFile;
    private final @NotNull UUID rejectDecisionFile;
}
