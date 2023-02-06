package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.repository.entity.Contract;

public interface IContractValidator {
    void validateEntity(Contract contract);
}
