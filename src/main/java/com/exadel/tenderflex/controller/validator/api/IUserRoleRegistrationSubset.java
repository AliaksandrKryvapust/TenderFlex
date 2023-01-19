package com.exadel.tenderflex.controller.validator.api;

import com.exadel.tenderflex.controller.validator.UserRoleRegistrationSubsetValidator;
import com.exadel.tenderflex.repository.entity.EUserRole;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UserRoleRegistrationSubsetValidator.class)
public @interface IUserRoleRegistrationSubset {
    EUserRole[] anyOf();
    String message() default "must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
