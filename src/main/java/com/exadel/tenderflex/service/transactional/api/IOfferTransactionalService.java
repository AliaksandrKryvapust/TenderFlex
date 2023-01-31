package com.exadel.tenderflex.service.transactional.api;

import com.exadel.tenderflex.repository.entity.Offer;

import java.util.UUID;

public interface IOfferTransactionalService  {
    Offer saveTransactional(Offer offer, UUID tenderId);
}
