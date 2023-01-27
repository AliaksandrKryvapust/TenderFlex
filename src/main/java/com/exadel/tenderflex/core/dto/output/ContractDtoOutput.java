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
public class ContractDtoOutput {
    private final @NotNull String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NotNull LocalDate contractDeadline;
    private final @Nullable String contractFile;
    private final @Nullable String awardDecisionFile;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
