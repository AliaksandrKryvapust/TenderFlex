package com.exadel.tenderflex.controller.exceptions.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class SingleExceptionDto {
    private final @NonNull String logref;
    private final @NonNull String message;
}
