package com.exadel.tenderflex.core.dto.input;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Builder
@Data
@Jacksonized
public class ContactPersonDtoInput {
    @NotNull(message = "contact name cannot be null")
    @Size(min = 2, max = 50, message = "contact name should contain from 2 to 50 letters")
    private final String name;
    @NotNull(message = "contact surname cannot be null")
    @Size(min = 2, max = 50, message = "contact surname should contain from 2 to 50 letters")
    private final String surname;
    @NotNull(message = "phone number cannot be null")
    @Positive (message = "phone number should be positive")
    private final Long phoneNumber;
}
