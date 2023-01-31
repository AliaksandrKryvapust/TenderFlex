package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.service.api.ITenderService;
import com.exadel.tenderflex.service.transactional.api.IOfferTransactionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferTransactionalService implements IOfferTransactionalService {
    private final IOfferRepository offerRepository;
    private final ITenderService tenderService;

    @Override
    @Transactional
    public Offer saveTransactional(Offer offer, UUID tenderId) {
        Offer savedEntity = offerRepository.save(offer);
        if (tenderId != null) {
            tenderService.addOfferToTender(savedEntity, tenderId);
        }
        return offer;
    }
}
