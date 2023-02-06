package com.exadel.tenderflex.service.validator.api;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.Tender;

public interface ITenderValidator extends IValidator<Tender> {
    void validateAwardCondition(Offer selectedOffer, Tender currentEntity);
}
