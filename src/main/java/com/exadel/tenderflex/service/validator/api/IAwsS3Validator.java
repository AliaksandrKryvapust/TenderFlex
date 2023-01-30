package com.exadel.tenderflex.service.validator.api;

public interface IAwsS3Validator {
    void validateFileKeyToStorage(String bucketName, String fileName);
}
