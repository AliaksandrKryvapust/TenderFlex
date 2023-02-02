package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.service.validator.api.ICompanyDetailsValidator;
import com.exadel.tenderflex.service.validator.api.IContactPersonValidator;
import com.exadel.tenderflex.service.validator.api.IOfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;

@Component
@RequiredArgsConstructor
public class OfferValidator implements IOfferValidator {
    private final ICompanyDetailsValidator companyDetailsValidator;
    private final IContactPersonValidator contactPersonValidator;

    @Override
    public void validateEntity(Offer offer) {
        checkAuxiliaryFields(offer);
        checkMandatoryFields(offer);
        companyDetailsValidator.validateEntity(offer.getBidder());
        contactPersonValidator.validateEntity(offer.getContactPerson());
        checkBidPrice(offer);
        checkCurrency(offer);
        checkOfferStatus(offer);
    }

    @Override
    public void optimisticLockCheck(Long version, Offer currentEntity) {
        Long currentVersion = currentEntity.getDtUpdate().toEpochMilli();
        if (!currentVersion.equals(version)) {
            throw new OptimisticLockException("offer table update failed, version does not match update denied");
        }
    }

    private void checkAuxiliaryFields(Offer offer) {
        if (offer.getId() != null) {
            throw new IllegalStateException("Offer id should be empty for offer: " + offer);
        }
        if (offer.getDtUpdate() != null) {
            throw new IllegalStateException("Offer version should be empty for offer: " + offer);
        }
        if (offer.getDtCreate() != null) {
            throw new IllegalStateException("Offer creation date should be empty for offer: " + offer);
        }
    }

    private void checkMandatoryFields(Offer offer) {
        if (offer.getContactPerson() == null) {
            throw new IllegalStateException("Contact person should not be empty for offer: " + offer);
        }
        if (offer.getBidder() == null) {
            throw new IllegalStateException("Company details should not be empty for offer: " + offer);
        }
        if (offer.getUser() == null) {
            throw new IllegalStateException("User should not be empty for offer: " + offer);
        }
        if (offer.getPropositionFile() == null) {
            throw new IllegalStateException("Proposition file should not be empty for offer: " + offer);
        }
    }

    private void checkBidPrice(Offer offer) {
        if (offer.getBidPrice() == null) {
            throw new IllegalArgumentException("Bid price is not valid for offer:" + offer);
        } else {
            if (offer.getBidPrice() <= 0) {
                throw new IllegalArgumentException("Bid should be positive for offer:" + offer);
            }
        }
    }

    private void checkCurrency(Offer offer) {
        if (offer.getCurrency() == null) {
            throw new IllegalArgumentException("currency is not valid for offer:" + offer);
        }
    }

    private void checkOfferStatus(Offer offer) {
        if (offer.getOfferStatusBidder() == null) {
            throw new IllegalArgumentException("offer status bidder is not valid for offer:" + offer);
        }
    }

    private void checkOfferStatusContractor(Offer offer) {
        if (offer.getOfferStatusContractor() == null) {
            throw new IllegalArgumentException("offer status contractor is not valid for offer:" + offer);
        }
    }
}
