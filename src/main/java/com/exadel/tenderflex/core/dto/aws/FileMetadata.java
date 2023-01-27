package com.exadel.tenderflex.core.dto.aws;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class FileMetadata extends ObjectMetadata {
    private final @NonNull String contentType;
    private final @NonNull String contentLength;
}
