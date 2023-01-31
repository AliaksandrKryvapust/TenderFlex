package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class ContactPersonDtoOutput {
    private final @NonNull String name;
    private final @NonNull String surname;
    private final @NonNull Long phoneNumber;
}
