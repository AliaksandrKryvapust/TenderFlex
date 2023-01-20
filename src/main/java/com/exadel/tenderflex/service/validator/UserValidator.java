package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.service.validator.api.IUserValidator;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;

@Component
public class UserValidator implements IUserValidator {


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
}
