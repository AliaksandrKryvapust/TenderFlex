package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.ContactPerson;
import com.exadel.tenderflex.service.validator.api.IContactPersonValidator;
import org.springframework.stereotype.Component;

@Component
public class ContactPersonValidator implements IContactPersonValidator {

    @Override
    public void validateEntity(ContactPerson contactPerson) {
        checkName(contactPerson);
        checkSurname(contactPerson);
        checkPhoneNumber(contactPerson);
    }

    private void checkName(ContactPerson contactPerson) {
        if (contactPerson.getName() == null || contactPerson.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not valid for contactPerson:" + contactPerson);
        }
        char[] chars = contactPerson.getName().toCharArray();
        if (chars.length < 2 || chars.length > 50) {
            throw new IllegalArgumentException("Name should contain from 2 to 50 letters for contactPerson:" + contactPerson);
        }
    }

    private void checkSurname(ContactPerson contactPerson) {
        if (contactPerson.getSurname() == null || contactPerson.getSurname().isBlank()) {
            throw new IllegalArgumentException("Surname is not valid for contactPerson:" + contactPerson);
        }
        char[] chars = contactPerson.getSurname().toCharArray();
        if (chars.length < 2 || chars.length > 50) {
            throw new IllegalArgumentException("Surname should contain from 2 to 50 letters for contactPerson:" + contactPerson);
        }
    }

    private void checkPhoneNumber(ContactPerson contactPerson) {
        if (contactPerson.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Phone number is not valid for contactPerson:" + contactPerson);
        }
        if (contactPerson.getPhoneNumber() <= 0) {
            throw new IllegalArgumentException("Phone number should be positive for contactPerson:" + contactPerson);
        }
    }
}
