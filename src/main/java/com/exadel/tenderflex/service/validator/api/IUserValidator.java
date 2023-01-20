package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.repository.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserValidator extends IValidator<User>{
    void validateUser(UserDtoLogin userDtoLogin, UserDetails userDetails);
}
