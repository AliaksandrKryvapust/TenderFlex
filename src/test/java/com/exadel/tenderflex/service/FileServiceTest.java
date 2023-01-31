package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.FileDtoInput;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.core.mapper.FileMapper;
import com.exadel.tenderflex.repository.api.IFileRepository;
import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.validator.api.IFileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @InjectMocks
    private FileService fileService;
    @Mock
    private IFileRepository fileRepository;
    @Mock
    private IAwsS3Service awsS3Service;
    @Mock
    private IFileValidator fileValidator;
    @Mock
    private FileMapper fileMapper;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb250cmFjdG9yQGdtYWlsLmNvbSIsImlhdCI6MTY3NDU2Nzk1OCwiZXhwIjoxNjc0NTcxNTU4fQ.nSk39xKnJPOmuci9TLzIzZyya-sTIm9IZIgAINNFRCw";
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";

    @Test
    void update() {
        // preconditions
        final File fileOutput = getPreparedFileOutput();
        Mockito.when(fileRepository.findById(id)).thenReturn(Optional.of(fileOutput));
        Mockito.when(awsS3Service.generateUrl(fileName)).thenReturn(url);
        Mockito.when(fileRepository.save(fileOutput)).thenReturn(fileOutput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<File> actualFile = ArgumentCaptor.forClass(File.class);

        //test
        File actual = fileService.update(fileOutput, id, dtUpdate.toEpochMilli());
        Mockito.verify(fileValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualFile.capture());
        Mockito.verify(fileMapper, Mockito.times(1)).updateEntityFields(actualFile.capture(),
                actualFile.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(fileOutput, actualFile.getValue());
        assertNotNull(actual);
        checkFileOutputFields(actual);
    }

    @Test
    void updateDto() {
        // preconditions
        final File fileOutput = getPreparedFileOutput();
        final FileDtoInput dtoInput = getPreparedFileDtoInput();
        final FileDtoOutput dtoOutput = getPreparedFileDtoOutput();
        Mockito.when(fileMapper.inputMapping(dtoInput)).thenReturn(fileOutput);
        Mockito.when(fileRepository.findById(id)).thenReturn(Optional.of(fileOutput));
        Mockito.when(awsS3Service.generateUrl(fileName)).thenReturn(url);
        Mockito.when(fileRepository.save(fileOutput)).thenReturn(fileOutput);
        Mockito.when(fileMapper.outputMapping(fileOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<File> actualFile = ArgumentCaptor.forClass(File.class);

        //test
        FileDtoOutput actual = fileService.updateDto(dtoInput, id, dtUpdate.toEpochMilli());
        Mockito.verify(fileValidator, Mockito.times(1)).validateEntity(actualFile.capture());
        Mockito.verify(fileValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualFile.capture());
        Mockito.verify(fileMapper, Mockito.times(1)).updateEntityFields(actualFile.capture(),
                actualFile.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(fileOutput, actualFile.getValue());
        assertNotNull(actual);
        checkFileDtoOutputFields(actual);
    }

    File getPreparedFileOutput() {
        return File.builder()
                .id(id)
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .fileKey(token)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    FileDtoInput getPreparedFileDtoInput() {
        return FileDtoInput.builder()
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .build();
    }

    FileDtoOutput getPreparedFileDtoOutput() {
        return FileDtoOutput.builder()
                .id(id.toString())
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }


    private void checkFileOutputFields(File actual) {
        assertEquals(id, actual.getId());
        assertEquals(fileName, actual.getFileName());
        assertEquals(token, actual.getFileKey());
        assertEquals(contentType, actual.getContentType());
        assertEquals(url, actual.getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getFileType());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
    }

    private void checkFileDtoOutputFields(FileDtoOutput actual) {
        assertEquals(id.toString(), actual.getId());
        assertEquals(fileName, actual.getFileName());
        assertEquals(contentType, actual.getContentType());
        assertEquals(url, actual.getUrl());
        assertEquals(EFileType.AWARD_DECISION.name(), actual.getFileType());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
    }
}