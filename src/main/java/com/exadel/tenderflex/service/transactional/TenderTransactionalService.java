package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.repository.api.IContractRepository;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.api.IRejectDecisionRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Contract;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.RejectDecision;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import com.exadel.tenderflex.service.transactional.api.ITenderTransactionalService;
import com.exadel.tenderflex.service.validator.api.IContractValidator;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenderTransactionalService implements ITenderTransactionalService {
    private final ITenderRepository tenderRepository;
    private final IRejectDecisionRepository rejectDecisionRepository;
    private final IContractRepository contractRepository;
    private final IOfferRepository offerRepository;
    private final IContractValidator contractValidator;
    private final ITenderValidator tenderValidator;

    @Override
    @Transactional
    public Tender saveTransactional(Tender tender) {
        Tender savedEntity = tenderRepository.save(tender);
        saveContract(savedEntity);
        saveRejectDecision(savedEntity);
        return savedEntity;
    }

    @Override
    @Transactional
    public Tender awardTransactionalAction(ActionDto actionDto) {
        Tender currentEntity = tenderRepository.findById(actionDto.getTender()).orElseThrow(NoSuchElementException::new);
        Contract contract = currentEntity.getContract();
        Offer selectedOffer = currentEntity.getOffers().stream().filter((i) -> i.getId().equals(actionDto.getOffer()))
                .collect(Collectors.toSet()).stream().findFirst().orElseThrow(NoSuchElementException::new);
        tenderValidator.validateAwardCondition(selectedOffer, currentEntity);
        saveOffer(selectedOffer);
        saveContract(contract, selectedOffer);
        return currentEntity;
    }

    private void saveRejectDecision(Tender tender) {
        RejectDecision rejectDecision = tender.getRejectDecision();
        rejectDecision.setTender(tender);
        rejectDecisionRepository.save(rejectDecision);
    }

    private void saveContract(Tender tender) {
        Contract contract = tender.getContract();
        contract.setTender(tender);
        contractRepository.save(contract);
    }

    private void saveOffer(Offer selectedOffer) {
        selectedOffer.setOfferStatusContractor(EOfferStatus.OFFER_SELECTED);
        selectedOffer.setOfferStatusBidder(EOfferStatus.OFFER_SELECTED_BY_CONTRACTOR);
        offerRepository.save(selectedOffer);
    }

    private void saveContract(Contract contract, Offer selectedOffer) {
        contractValidator.validateEntity(contract);
        contract.setOffer(selectedOffer);
        contractRepository.save(contract);
    }
}
