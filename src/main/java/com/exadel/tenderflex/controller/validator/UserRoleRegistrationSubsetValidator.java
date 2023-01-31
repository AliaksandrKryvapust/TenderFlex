package com.exadel.tenderflex.controller.validator;

import com.exadel.tenderflex.controller.validator.api.IUserRoleRegistrationSubset;
import com.exadel.tenderflex.repository.entity.enums.EUserRole;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class UserRoleRegistrationSubsetValidator implements ConstraintValidator<IUserRoleRegistrationSubset, EUserRole> {
    private EUserRole[] subset;

    @Override
    public void initialize(IUserRoleRegistrationSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(EUserRole value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
