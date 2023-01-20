package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.validator.api.IUserDetailsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserDetailsValidator implements IUserDetailsValidator {
    private final PasswordEncoder encoder;

    @Override
    public void validateLogin(UserDtoLogin userDtoLogin, UserDetails userDetails) {
        if (!encoder.matches(userDtoLogin.getPassword(), userDetails.getPassword()) || !userDetails.isEnabled()) {
            throw new BadCredentialsException("User login or password is incorrect or user is not activated");
        }
    }

    @Override
    public void validate(String email, User user) {
        if (user == null) {
            throw new NoSuchElementException("There is no such user" + email);
        }
    }
}
