package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;

public interface IUserManager extends IManager<UserDtoOutput, UserDtoInput, UserDtoOutput>,
        IManagerUpdate<UserDtoOutput, UserDtoInput> {
    UserLoginDtoOutput saveUser(UserDtoRegistration user);
    UserDtoOutput getUserDto(String email);
}
