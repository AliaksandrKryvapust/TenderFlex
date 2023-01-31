package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.service.validator.api.IFileValidator;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;

import static com.exadel.tenderflex.core.Constants.BUCKET_NAME;

@Component
public class FileValidator implements IFileValidator {

    @Override
    public void validateEntity(File file) {
        checkAuxiliaryFields(file);
        checkFileType(file);
        checkContentType(file);
        checkFileName(file);
        checkUrl(file);
        checkFileKey(file);
    }

    @Override
    public void optimisticLockCheck(Long version, File currentEntity) {
        Long currentVersion = currentEntity.getDtUpdate().toEpochMilli();
        if (!currentVersion.equals(version)) {
            throw new OptimisticLockException("file table update failed, version does not match update denied");
        }
    }

    private void checkAuxiliaryFields(File file) {
        if (file.getId() != null) {
            throw new IllegalStateException("File id should be empty for file: " + file);
        }
        if (file.getDtUpdate() != null) {
            throw new IllegalStateException("File version should be empty for file: " + file);
        }
        if (file.getDtCreate() != null) {
            throw new IllegalStateException("File creation date should be empty for file: " + file);
        }
    }

    private void checkFileType(File file) {
        if (file.getFileType() == null) {
            throw new IllegalStateException("file type is not valid for file: " + file);
        }
    }

    private void checkContentType(File file) {
        if (file.getContentType() == null || file.getContentType().isBlank() || !file.getContentType().contains("/") ) {
            throw new IllegalStateException("Content type is not valid for file: " + file);
        }
    }

    private void checkFileName(File file) {
        if (file.getFileName() == null || file.getFileName().isBlank() || !file.getFileName().contains(".")) {
            throw new IllegalStateException("File name is not valid for file: " + file);
        }
    }

    private void checkUrl(File file) {
        if (file.getUrl() == null || file.getUrl().isBlank() || !file.getUrl().contains(BUCKET_NAME)) {
            throw new IllegalStateException("url is not valid for file: " + file);
        }
    }

    private void checkFileKey(File file) {
        if (file.getFileKey() == null || file.getFileKey().isBlank()) {
            throw new IllegalStateException("file key is not valid for file: " + file);
        }
    }
}
