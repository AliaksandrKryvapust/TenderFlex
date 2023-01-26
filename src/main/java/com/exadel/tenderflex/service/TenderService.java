package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.mapper.TenderMapper;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.service.api.ITenderService;
import com.exadel.tenderflex.service.transactional.api.ITenderTransactionalService;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenderService implements ITenderService {
    private final ITenderRepository tenderRepository;
    private final ITenderValidator tenderValidator;
    private final TenderMapper tenderMapper;
    private final ITenderTransactionalService tenderTransactionalService;

    @Override
    public Tender save(Tender tender) {
//        return tenderTransactionalService.saveTransactional(tender);
        return tenderRepository.save(tender);
    }

    @Override
    public Page<Tender> get(Pageable pageable) {
        return tenderRepository.findAll(pageable);
    }

    @Override
    public Tender get(UUID id) {
        return tenderRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Tender update(Tender tender, UUID id, Long version) {
        Tender currentEntity = get(id);
        tenderValidator.optimisticLockCheck(version, currentEntity);
        tenderMapper.updateEntityFields(tender, currentEntity);
        return save(currentEntity);
    }
}
