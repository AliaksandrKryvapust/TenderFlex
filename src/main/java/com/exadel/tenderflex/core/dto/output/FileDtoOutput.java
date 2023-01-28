package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class FileDtoOutput {
    private final @NonNull String id;
    private final @NonNull String fileType;
    private final @NonNull String contentType;
    private final @NonNull String fileName;
    private final @NonNull String url;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
