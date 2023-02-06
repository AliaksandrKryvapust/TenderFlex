package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.repository.entity.ContactPerson;

public interface IContactPersonValidator {
    void validateEntity(ContactPerson contactPerson);
}
