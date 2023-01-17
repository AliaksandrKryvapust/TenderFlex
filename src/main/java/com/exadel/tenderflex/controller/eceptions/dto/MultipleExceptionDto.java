package com.exadel.tenderflex.controller.eceptions.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Builder
@Data
public class MultipleExceptionDto {
    private final @NonNull String logref = "structured_error";
    private final @NonNull List<ExceptionDto> errors;
}
