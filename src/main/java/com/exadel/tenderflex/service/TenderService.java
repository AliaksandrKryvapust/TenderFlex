package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import com.exadel.tenderflex.core.mapper.TenderMapper;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.User;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenderService implements ITenderService, ITenderManager {
    private final ITenderRepository tenderRepository;
    private final ITenderValidator tenderValidator;
    private final TenderMapper tenderMapper;
    private final UserService userService;
    private final ITenderTransactionalService tenderTransactionalService;

    @Override
    public Tender save(Tender tender) {
        return tenderTransactionalService.saveTransactional(tender);
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

    @Override
    public TenderDtoOutput saveDto(TenderDtoInput dtoInput) {
        User currentUser = getUserFromSecurityContext();
        Tender entityToSave = tenderMapper.inputMapping(dtoInput, currentUser);
        tenderValidator.validateEntity(entityToSave);
        Tender tender = save(entityToSave);
        return tenderMapper.outputMapping(tender);
    }

    @Override
    public PageDtoOutput<TenderPageDtoOutput> getDto(Pageable pageable) {
        return tenderMapper.outputPageMapping(get(pageable));
    }

    @Override
    public TenderDtoOutput getDto(UUID id) {
        Tender tender = get(id);
        return tenderMapper.outputMapping(tender);
    }

    @Override
    public TenderDtoOutput updateDto(TenderDtoInput dtoInput, UUID id, Long version) {
        User currentUser = getUserFromSecurityContext();
        Tender entityToSave = tenderMapper.inputMapping(dtoInput, currentUser);
        tenderValidator.validateEntity(entityToSave);
        Tender tender = update(entityToSave, id, version);
        return tenderMapper.outputMapping(tender);
    }

    private User getUserFromSecurityContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(userDetails.getUsername());
    }

    @Override
    public TenderDtoOutput saveFormData(String tenderJson, List<MultipartFile> files) {
        TenderDtoInput dtoInput = tenderMapper.extractJson(tenderJson);
        int fileCountTest = files.size();
        User currentUser = getUserFromSecurityContext();
        Tender entityToSave = tenderMapper.inputMapping(dtoInput, currentUser);
        tenderValidator.validateEntity(entityToSave);
        Tender tender = save(entityToSave);
        return tenderMapper.outputMapping(tender);
    }
}
