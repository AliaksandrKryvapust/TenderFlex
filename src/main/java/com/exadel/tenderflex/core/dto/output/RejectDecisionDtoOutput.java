package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Builder
@Data
public class RejectDecisionDtoOutput {
    private final @NotNull String id;
    private final @NotNull FileDtoOutput rejectDecision;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
