package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.api.IRejectDecisionRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.RejectDecision;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import com.exadel.tenderflex.service.transactional.api.IOfferTransactionalService;
import com.exadel.tenderflex.service.validator.api.IOfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferTransactionalService implements IOfferTransactionalService {
    private final IOfferRepository offerRepository;
    private final ITenderRepository tenderRepository;
    private final IRejectDecisionRepository rejectDecisionRepository;
    private final IOfferValidator offerValidator;

    @Override
    @Transactional
    public Offer saveTransactional(Offer offer) {
        Offer savedEntity = offerRepository.save(offer);
        if (offer.getTenderId() != null) {
            addOfferToTender(savedEntity);
        }
        return savedEntity;
    }

    @Override
    @Transactional
    public Offer awardTransactionalAction(ActionDto actionDto) {
        Offer currentEntity = offerRepository.findById(actionDto.getOffer()).orElseThrow(NoSuchElementException::new);
        offerValidator.validateAwardDecision(currentEntity);
        Tender tender = currentEntity.getTender();
        Set<Offer> activeOffers = tender.getOffers().stream()
                .filter((i)->i.getOfferStatusContractor().equals(EOfferStatus.OFFER_RECEIVED))
                .collect(Collectors.toSet());
        if (Boolean.TRUE.equals(actionDto.getAward())){
            positiveAwardAction(currentEntity, tender, activeOffers);
        } else {
            negativeAwardAction(currentEntity, tender, activeOffers);
        }
        return currentEntity;
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

    private void saveOffer(Offer currentEntity, EOfferStatus contractApprovedByBidder) {
        currentEntity.setOfferStatusContractor(contractApprovedByBidder);
        currentEntity.setOfferStatusBidder(contractApprovedByBidder);
        offerRepository.save(currentEntity);
    }

    private void negativeAwardAction(Offer currentEntity, Tender tender, Set<Offer> activeOffers) {
        saveOffer(currentEntity, EOfferStatus.CONTRACT_DECLINED_BY_BIDDER);
        if (activeOffers.size()==0){
            tender.setTenderStatus(ETenderStatus.CLOSED);
            tenderRepository.save(tender);
        } else {
            currentEntity.setActive(false);
            offerRepository.save(currentEntity);
        }
    }

    private void positiveAwardAction(Offer currentEntity, Tender tender, Set<Offer> activeOffers) {
        saveOffer(currentEntity, EOfferStatus.CONTRACT_APPROVED_BY_BIDDER);
        tender.setTenderStatus(ETenderStatus.CLOSED);
        tenderRepository.save(tender);
        activeOffers.forEach((i)->saveOffer(i, EOfferStatus.OFFER_REJECTED_BY_CONTRACTOR));
        saveRejectDecision(tender, activeOffers);
    }

    private void saveRejectDecision(Tender tender, Set<Offer> activeOffers) {
        RejectDecision rejectDecision = tender.getRejectDecision();
        rejectDecision.setOffers(activeOffers);
        rejectDecisionRepository.save(rejectDecision);
    }
}
