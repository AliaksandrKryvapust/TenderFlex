package com.exadel.tenderflex.core.dto.output;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class UserLoginDtoOutput {
    private final @NonNull String email;
    private final @NonNull String role;
    private final @NonNull Long duration;
    private final String token;
}
