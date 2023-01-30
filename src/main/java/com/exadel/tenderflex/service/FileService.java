package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.FileDtoInput;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.core.mapper.FileMapper;
import com.exadel.tenderflex.repository.api.IFileRepository;
import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.api.IFileManager;
import com.exadel.tenderflex.service.api.IFileService;
import com.exadel.tenderflex.service.validator.api.IFileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService, IFileManager {
    private final IFileRepository fileRepository;
    private final IAwsS3Service awsS3Service;
    private final IFileValidator fileValidator;
    private final FileMapper fileMapper;

    private File save(File file) {
        return fileRepository.save(file);
    }

    private File get(UUID id) {
        return fileRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public File update(File file, UUID id, Long version) {
        File currentEntity = get(id);
        fileValidator.optimisticLockCheck(version, currentEntity);
        fileMapper.updateEntityFields(file, currentEntity);
        updateUrl(file);
        return save(currentEntity);
    }

    @Override
    public FileDtoOutput updateDto(FileDtoInput dtoInput, UUID id, Long version) {
        File entityToSave = fileMapper.inputMapping(dtoInput);
        fileValidator.validateEntity(entityToSave);
        File file = update(entityToSave, id, version);
        return fileMapper.outputMapping(file);
    }

    private void updateUrl(File file) {
        String url = awsS3Service.generateUrl(file.getFileName());
        file.setUrl(url);
    }
}
