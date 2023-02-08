package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.Contract;
import com.exadel.tenderflex.service.validator.api.IContractValidator;
import org.springframework.stereotype.Component;

@Component
public class ContractValidator implements IContractValidator {
    @Override
    public void validateEntity(Contract contract) {
        if (contract.getOffer() != null) {
            throw new IllegalStateException("It`s forbidden to chose 2 offers at once: " + contract);
        }
    }
}
