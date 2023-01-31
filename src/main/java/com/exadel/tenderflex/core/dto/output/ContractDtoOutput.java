package com.exadel.tenderflex.core.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class ContractDtoOutput {
    private final @NonNull String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @NonNull LocalDate contractDeadline;
    private final @NonNull Set<FileDtoOutput> files;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
