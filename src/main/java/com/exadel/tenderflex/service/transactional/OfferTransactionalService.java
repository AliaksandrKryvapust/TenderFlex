package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.service.transactional.api.IOfferTransactionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OfferTransactionalService implements IOfferTransactionalService {
    private final IOfferRepository offerRepository;
    private final ITenderRepository tenderRepository;

    @Override
    @Transactional
    public Offer saveTransactional(Offer offer) {
        Offer savedEntity = offerRepository.save(offer);
        if (offer.getTenderId() != null) {
            addOfferToTender(savedEntity);
        }
        return savedEntity;
    }

    private void addOfferToTender(Offer offer) {
        Tender currentEntity = tenderRepository.findById(offer.getTenderId()).orElseThrow(NoSuchElementException::new);
        updateOffersSet(offer, currentEntity);
        tenderRepository.save(currentEntity);
    }

    private void updateOffersSet(Offer offer, Tender currentEntity) {
        Set<Offer> offers = currentEntity.getOffers();
        offers.add(offer);
        currentEntity.setOffers(offers);
    }
}
