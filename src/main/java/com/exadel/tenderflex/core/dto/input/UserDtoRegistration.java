package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IUserRoleRegistrationSubset;
import com.exadel.tenderflex.controller.validator.api.IValidEmail;
import com.exadel.tenderflex.repository.entity.EUserRole;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@Jacksonized
public class UserDtoRegistration {
    @NotNull(message = "username cannot be null")
    @Size(min = 2, max = 50, message = "username should contain from 2 to 50 letters")
    private final String username;
    @NotNull(message = "password cannot be null")
    @Size(min = 2, max = 200, message = "password should contain from 2 to 200 letters")
    private final String password;
    @NotNull(message = "email cannot be null")
    @NotEmpty
    @IValidEmail(message = "email is not valid")
    private final String email;
    @NotNull(message = "user role cannot be null")
    @IUserRoleRegistrationSubset(anyOf = {EUserRole.BIDDER, EUserRole.CONTRACTOR}, message = "user role is not valid")
    private final EUserRole role;
}
