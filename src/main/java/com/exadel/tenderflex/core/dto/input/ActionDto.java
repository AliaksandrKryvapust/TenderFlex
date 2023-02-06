package com.exadel.tenderflex.core.dto.input;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class ActionDto {
    @NotNull(message = "tender cannot be null")
    private final UUID tender;
    @NotNull(message = "offer cannot be null")
    private final UUID offer;
    @NotNull(message = "action cannot be null")
    private final Boolean award;
}
