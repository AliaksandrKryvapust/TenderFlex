package com.exadel.tenderflex.service.transactional;

import com.exadel.tenderflex.repository.api.IContractRepository;
import com.exadel.tenderflex.repository.api.IRejectDecisionRepository;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Contract;
import com.exadel.tenderflex.repository.entity.RejectDecision;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.service.transactional.api.ITenderTransactionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenderTransactionalService implements ITenderTransactionalService {
    private final ITenderRepository tenderRepository;
    private final IRejectDecisionRepository rejectDecisionRepository;
    private final IContractRepository contractRepository;

    @Override
    @Transactional
    public Tender saveTransactional(Tender tender) {
        Tender savedEntity = tenderRepository.save(tender);
        saveContract(savedEntity);
        saveRejectDecision(savedEntity);
        return savedEntity;
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
}
