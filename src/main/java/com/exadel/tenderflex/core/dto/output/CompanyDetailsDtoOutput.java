package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@Builder
@Data
public class CompanyDetailsDtoOutput {
    private final @NonNull String officialName;
    private final @NonNull String registrationNumber;
    private final @NonNull String country;
    private final @Nullable String town;
}
