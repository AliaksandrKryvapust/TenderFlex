package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.repository.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserDetailsValidator {
    void validateLogin(UserDtoLogin userDtoLogin, UserDetails userDetails);
    void validate(String email, User user);
}
