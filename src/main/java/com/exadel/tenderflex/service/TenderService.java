package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForContractorDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForContractorDtoOutput;
import com.exadel.tenderflex.core.mapper.OfferMapper;
import com.exadel.tenderflex.core.mapper.TenderMapper;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.User;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.api.IOfferService;
import com.exadel.tenderflex.service.api.ITenderManager;
import com.exadel.tenderflex.service.api.ITenderService;
import com.exadel.tenderflex.service.transactional.api.ITenderTransactionalService;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenderService implements ITenderService, ITenderManager {
    private final ITenderRepository tenderRepository;
    private final ITenderValidator tenderValidator;
    private final TenderMapper tenderMapper;
    private final UserService userService;
    private final ITenderTransactionalService tenderTransactionalService;
    private final IAwsS3Service awsS3Service;
    private final IOfferService offerService;
    private final OfferMapper offerMapper;

    @Override
    public Tender save(Tender tender) {
        return tenderRepository.save(tender);
    }

    @Override
    public Tender saveInTransaction(Tender tender) {
        return tenderTransactionalService.saveTransactional(tender);
    }

    @Override
    public Page<Tender> get(Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return tenderRepository.findAllForUser(userDetails.getUsername(), pageable);
    }

    @Override
    public Page<Tender> getAll(Pageable pageable) {
        return tenderRepository.findAll(pageable);
    }

    @Override
    public Set<Tender> findExpiredSubmissionDeadline(LocalDate currentDate, ETenderStatus tenderStatus) {
        return tenderRepository.findAllBySubmissionDeadlineBeforeAndTenderStatus(currentDate, tenderStatus);
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
        return saveInTransaction(currentEntity);
    }

    @Override
    public TenderDtoOutput saveDto(String tenderJson, Map<EFileType, MultipartFile> files) {
        TenderDtoInput dtoInput = tenderMapper.extractJson(tenderJson);
        Map<EFileType, AwsS3FileDto> urls = awsS3Service.generateUrls(files);
        User currentUser = getUserFromSecurityContext();
        Tender entityToSave = tenderMapper.inputMapping(dtoInput, currentUser, files, urls);
        tenderValidator.validateEntity(entityToSave);
        Tender tender = saveInTransaction(entityToSave);
        return tenderMapper.outputMapping(tender);
    }

    @Override
    public PageDtoOutput<TenderPageForContractorDtoOutput> getDto(Pageable pageable) {
        Page<Tender> page = get(pageable);
        return tenderMapper.outputPageMapping(page);
    }

    @Override
    public PageDtoOutput<OfferPageForContractorDtoOutput> getOfferForTender(UUID id, Pageable pageable) {
        Page<Offer> page = offerService.getForTender(id, pageable);
        return offerMapper.outputContractorPageMapping(page);
    }

    @Override
    public PageDtoOutput<OfferPageForContractorDtoOutput> getOfferForContractor(Pageable pageable) {
        Page<Offer> page = offerService.getForContractor(pageable);
        return offerMapper.outputContractorPageMapping(page);
    }

    @Override
    public TenderDtoOutput getDto(UUID id) {
        Tender tender = get(id);
        return tenderMapper.outputMapping(tender);
    }

    @Override
    public PageDtoOutput<TenderPageForBidderDtoOutput> getDtoAll(Pageable pageable) {
        Page<Tender> page = getAll(pageable);
        return tenderMapper.outputPageForBidderMapping(page);
    }

    @Override
    public TenderDtoOutput updateDto(String tenderJson, Map<EFileType, MultipartFile> fileMap, UUID id, Long version) {
        TenderDtoInput dtoInput = tenderMapper.extractJson(tenderJson);
        Map<EFileType, AwsS3FileDto> urls = awsS3Service.generateUrls(fileMap);
        User currentUser = getUserFromSecurityContext();
        Tender entityToSave = tenderMapper.inputMapping(dtoInput, currentUser, fileMap, urls);
        tenderValidator.validateEntity(entityToSave);
        Tender tender = update(entityToSave, id, version);
        return tenderMapper.outputMapping(tender);
    }

    @Override
    public TenderDtoOutput awardAction(ActionDto actionDto) {
        Tender tender = tenderTransactionalService.awardTransactionalAction(actionDto);
        return tenderMapper.outputMapping(tender);
    }

    private User getUserFromSecurityContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(userDetails.getUsername());
    }
}
