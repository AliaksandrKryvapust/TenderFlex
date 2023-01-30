package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@Jacksonized
public class FileDtoInput {
    @NotNull(message = "file type cannot be null")
    @IValidEnum(enumClass = EFileType.class, message = "file type does not match")
    private final @NonNull String fileType;
    @NotBlank(message = "content type cannot be empty")
    private final @NonNull String contentType;
    @NotBlank(message = "content type cannot be empty")
    private final @NonNull String fileName;
    @NotBlank(message = "content type cannot be empty")
    private final @NonNull String url;
}
