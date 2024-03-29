package com.exadel.tenderflex.core.dto.output;

import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.exadel.tenderflex.repository.entity.enums.EUserStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@Builder
@Data
public class UserDtoOutput {
    private final @NonNull String id;
    private final @NonNull String username;
    private final @NonNull String email;
    private final @NonNull EUserRole role;
    private final @NonNull EUserStatus status;
    private final @NonNull Instant dtCreate;
    private final @NonNull Instant dtUpdate;
}
