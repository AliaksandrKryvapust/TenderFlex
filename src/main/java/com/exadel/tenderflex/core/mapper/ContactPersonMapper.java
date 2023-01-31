package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.ContactPersonDtoInput;
import com.exadel.tenderflex.core.dto.output.ContactPersonDtoOutput;
import com.exadel.tenderflex.repository.entity.ContactPerson;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContactPersonMapper {
    public ContactPerson inputMapping(ContactPersonDtoInput dtoInput){
        return ContactPerson.builder()
                .name(dtoInput.getName())
                .surname(dtoInput.getSurname())
                .phoneNumber(dtoInput.getPhoneNumber())
                .build();
    }

    public ContactPersonDtoOutput outputMapping(ContactPerson contactPerson){
        return ContactPersonDtoOutput.builder()
                .name(contactPerson.getName())
                .surname(contactPerson.getSurname())
                .phoneNumber(contactPerson.getPhoneNumber())
                .build();
    }
}
