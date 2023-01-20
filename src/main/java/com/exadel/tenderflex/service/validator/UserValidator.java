package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;

@Component
@RequiredArgsConstructor
public class UserValidator implements IUserValidator {
    private final PasswordEncoder encoder;

    @Override
    public void validateEntity(User user) {
        if (user.getId() != null || user.getDtUpdate() != null) {
            throw new IllegalStateException("User id should be empty");
        }
    }

    @Override
    public void optimisticLockCheck(Long version, User currentEntity) {
        Long currentVersion = currentEntity.getDtUpdate().toEpochMilli();
        if (!currentVersion.equals(version)) {
            throw new OptimisticLockException("user table update failed, version does not match update denied");
        }
    }

    @Override
    public void validateUser(UserDtoLogin userDtoLogin, UserDetails userDetails) {
        if (!encoder.matches(userDtoLogin.getPassword(), userDetails.getPassword()) || !userDetails.isEnabled()) {
            throw new BadCredentialsException("User login or password is incorrect or user is not activated");
        }
    }
}
