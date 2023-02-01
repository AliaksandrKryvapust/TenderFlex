package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.mapper.OfferMapper;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.api.IOfferService;
import com.exadel.tenderflex.service.api.IUserService;
import com.exadel.tenderflex.service.transactional.api.IOfferTransactionalService;
import com.exadel.tenderflex.service.validator.api.IOfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferService implements IOfferService {
    private final IOfferRepository offerRepository;
    private final IOfferValidator offerValidator;
    private final OfferMapper offerMapper;
    private final IUserService userService;
    private final IOfferTransactionalService offerTransactionalService;
    private final IAwsS3Service awsS3Service;

    @Override
    public Offer save(Offer offer) {
        return offerTransactionalService.saveTransactional(offer);
    }

    @Override
    public Page<Offer> get(Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return offerRepository.findAllForUser(userDetails.getUsername(), pageable);
    }

    @Override
    public Offer get(UUID id) {
        return offerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Offer update(Offer offer, UUID id, Long version) {
        Offer currentEntity = get(id);
        offerValidator.optimisticLockCheck(version, currentEntity);
        offerMapper.updateEntityFields(offer, currentEntity);
        return save(currentEntity);
    }
}
