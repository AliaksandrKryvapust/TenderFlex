package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {
    @InjectMocks
    private FileValidator fileValidator;

    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb250cmFjdG9yQGdtYWlsLmNvbSIsImlhdCI6MTY3NDU2Nzk1OCwiZXhwIjoxNjc0NTcxNTU4fQ.nSk39xKnJPOmuci9TLzIzZyya-sTIm9IZIgAINNFRCw";
    final String contentType = "application/pdf";
    final String fileName = "testFile.pdf";
    final String url = "https://javatests3111222.s3.eu-central-1.amazonaws.com/e6b8e68f-1457-451f-a4ff-5bb65212c8b6.pdf";

    @Test
    void validateNonEmptyId() {
        // preconditions
        final File file = getPreparedFileOutput();
        final String messageExpected = "File id should be empty for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyFileType() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileType(null);

        final String messageExpected = "file type is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyContentType() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setContentType(null);

        final String messageExpected = "Content type is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankContentType() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setContentType(" ");

        final String messageExpected = "Content type is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateWrongContentType() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setContentType("application");

        final String messageExpected = "Content type is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }


    @Test
    void validateEmptyFileName() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileName(null);

        final String messageExpected = "File name is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankFileName() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileName(" ");

        final String messageExpected = "File name is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateWrongFileName() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileName("test");

        final String messageExpected = "File name is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }


    @Test
    void validateEmptyUrl() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setUrl(null);

        final String messageExpected = "url is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankUrl() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setUrl(" ");

        final String messageExpected = "url is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateWrongUrl() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setUrl("localhost8080");

        final String messageExpected = "url is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateEmptyFileKey() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileKey(null);

        final String messageExpected = "file key is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void validateBlankFileKey() {
        // preconditions
        final File file = getPreparedFileInput();
        file.setFileKey(" ");

        final String messageExpected = "file key is not valid for file: " + file;

        //test
        Exception actualException = assertThrows(IllegalStateException.class, () -> fileValidator.validateEntity(file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
    }

    @Test
    void optimisticLockCheck() {
        // preconditions
        final File file = getPreparedFileOutput();
        final Long version = dtUpdate.toEpochMilli() - 1000;
        final String messageExpected = "file table update failed, version does not match update denied";

        //test
        Exception actualException = assertThrows(OptimisticLockException.class, () ->
                fileValidator.optimisticLockCheck(version, file));

        // assert
        Assertions.assertEquals(messageExpected, actualException.getMessage());
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

    File getPreparedFileInput() {
        return File.builder()
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .fileKey(token)
                .url(url)
                .build();
    }
}