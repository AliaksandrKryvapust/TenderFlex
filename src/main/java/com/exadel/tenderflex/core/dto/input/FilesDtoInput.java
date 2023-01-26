package com.exadel.tenderflex.core.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate contractDeadline;
    private final UUID contractFile;
    private final UUID awardDecisionFile;
    private final UUID rejectDecisionFile;
}
