package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class UserLoginDtoOutput {
    private final @NonNull String email;
    private final String token;
}
