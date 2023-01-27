package com.exadel.tenderflex.core.dto.aws;

import lombok.Data;
import lombok.NonNull;

@Data
public class AwsS3FileDto {
    private final @NonNull String url;
    private final @NonNull String fileKey;
}
