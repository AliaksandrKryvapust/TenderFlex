package com.exadel.tenderflex.core.dto.input;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class FilesDtoInput {
    @NotNull(message = "contract deadline cannot be null")
    @Future(message = "contract deadline should refer to moment in the future")
    private final LocalDate contractDeadline;
    private final UUID contractFile;
    private final UUID awardDecisionFile;
    private final UUID rejectDecisionFile;
}
