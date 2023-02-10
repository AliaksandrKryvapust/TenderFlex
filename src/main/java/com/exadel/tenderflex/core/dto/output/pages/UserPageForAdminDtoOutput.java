package com.exadel.tenderflex.core.dto.output.pages;

import com.exadel.tenderflex.repository.entity.enums.EUserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Builder
@Data
public class UserPageForAdminDtoOutput {
    private final @NonNull String id;
    private final @NonNull String email;
    private final @NonNull EUserRole role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final @Nullable LocalDate dtLogin;
}
