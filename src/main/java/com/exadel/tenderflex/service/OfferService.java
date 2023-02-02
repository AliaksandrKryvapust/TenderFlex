package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.input.OfferDtoInput;
import com.exadel.tenderflex.core.dto.output.OfferDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.mapper.OfferMapper;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.api.IOfferManager;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferService implements IOfferService, IOfferManager {
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

    @Override
    public Page<Offer> getForTender(UUID id, Pageable pageable) {
        return offerRepository.findAllForTender(id, pageable);
    }

    @Override
    public Page<Offer> getForContractor(Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return offerRepository.findAllForContractor(userDetails.getUsername(), pageable);
    }

    @Override
    public PageDtoOutput<OfferPageForBidderDtoOutput> getDto(Pageable pageable) {
        return offerMapper.outputBidderPageMapping(get(pageable));
    }

    @Override
    public OfferDtoOutput getDto(UUID id) {
        return offerMapper.outputMapping(get(id));
    }

    @Override
    public OfferDtoOutput saveDto(String offerJson, Map<EFileType, MultipartFile> fileMap) {
        OfferDtoInput dtoInput = offerMapper.extractJson(offerJson);
        Map<EFileType, AwsS3FileDto> urls = awsS3Service.generateUrls(fileMap);
        User currentUser = getUserFromSecurityContext();
        Offer entityToSave = offerMapper.inputMapping(dtoInput, currentUser, fileMap, urls);
        offerValidator.validateEntity(entityToSave);
        Offer offer = save(entityToSave);
        return offerMapper.outputMapping(offer);
    }

    @Override
    public OfferDtoOutput updateDto(String offerJson, Map<EFileType, MultipartFile> fileMap, UUID id, Long version) {
        OfferDtoInput dtoInput = offerMapper.extractJson(offerJson);
        Map<EFileType, AwsS3FileDto> urls = awsS3Service.generateUrls(fileMap);
        User currentUser = getUserFromSecurityContext();
        Offer entityToSave = offerMapper.inputMapping(dtoInput, currentUser, fileMap, urls);
        offerValidator.validateEntity(entityToSave);
        Offer offer = update(entityToSave, id, version);
        return offerMapper.outputMapping(offer);
    }

    private User getUserFromSecurityContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(userDetails.getUsername());
    }
}
