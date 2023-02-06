package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.ContactPerson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ContactPersonValidatorTest {
    @InjectMocks
    private ContactPersonValidator contactPersonValidator;

    // preconditions
    final String name = "Marek";
    final String surname = "KOWALSKI";
    final Long phoneNumber = 48251173301L;

    @Test
    void validateEmptyName() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setName(null);

        final String messageExpected = "Name is not valid for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortName() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setName("a");

        final String messageExpected = "Name should contain from 2 to 50 letters for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptySurname() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setSurname(null);

        final String messageExpected = "Surname is not valid for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateShortSurname() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setSurname("a");

        final String messageExpected = "Surname should contain from 2 to 50 letters for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyPhoneNumber() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setPhoneNumber(null);

        final String messageExpected = "Phone number is not valid for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateNegativePhoneNumber() {
        // preconditions
        final ContactPerson contactPerson = getPreparedContactPerson();
        contactPerson.setPhoneNumber(-1L);

        final String messageExpected = "Phone number should be positive for contactPerson:" + contactPerson;

        //test
        Exception actualException = assertThrows(IllegalArgumentException.class, () -> contactPersonValidator.validateEntity(contactPerson));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }


    ContactPerson getPreparedContactPerson() {
        return ContactPerson.builder()
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
                .build();
    }
}