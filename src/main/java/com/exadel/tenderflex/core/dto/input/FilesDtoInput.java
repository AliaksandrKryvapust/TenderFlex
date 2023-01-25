package com.exadel.tenderflex.core.dto.input;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class FilesDtoInput {
    @NotNull(message = "contract deadline cannot be null")
    @Future(message = "contract deadline should refer to moment in the future")
    private LocalDate contractDeadline;
    @NotNull(message = "contract file cannot be null")
    @NotBlank(message = "contract file cannot be blank")
    private UUID contractFile;
    @NotNull(message = "award decision file cannot be null")
    @NotBlank(message = "award decision file cannot be blank")
    private UUID awardDecisionFile;
    @NotNull(message = "reject decision file cannot be null")
    @NotBlank(message = "reject decision file cannot be blank")
    private UUID rejectDecisionFile;
}
