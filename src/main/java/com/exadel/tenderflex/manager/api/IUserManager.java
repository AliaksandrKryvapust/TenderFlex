package com.exadel.tenderflex.manager.api;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;

public interface IUserManager extends IManager<UserDtoOutput, UserDtoInput>, IManagerUpdate<UserDtoOutput, UserDtoInput>{
    UserLoginDtoOutput login(UserDtoLogin userDtoLogin);
    UserLoginDtoOutput saveUser(UserDtoRegistration user);
    UserDtoOutput getUser(String email);
}
